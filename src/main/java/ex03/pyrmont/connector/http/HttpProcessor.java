package ex03.pyrmont.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.StringManager;

import ex03.pyrmont.ServletProcessor;
import ex03.pyrmont.StaticResourceProcessor;

public class HttpProcessor {
	
	private HttpConnector connector;
	private HttpRequest request;
	private HttpResponse response;
	private HttpRequestLine requestLine = new HttpRequestLine();
	
	protected StringManager sm = StringManager.getManager("ex03.pyrmont.connector.http");
	
	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}
	
	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;
		
		try {
			input = new SocketInputStream(socket.getInputStream(), 2048);
			output = socket.getOutputStream();
			//构造request和response，每个process只有单一的request和response
			request = new HttpRequest(input);
			response = new HttpResponse(output);
			response.setRequest(this.request);
			response.setHeader("Server", "Pyrmont Servlet Container");
			
			parseRequeset(input, output);
			parseHeader(input);
			
			//该转发了
			if(this.request.getRequestURI().startsWith("/servlet/")) {
				ServletProcessor processor = new ServletProcessor();
				processor.process(request, response);
			}else {
				StaticResourceProcessor processor = new StaticResourceProcessor();
				processor.process(request, response);
			}
			//这时请求已经被完全处理完毕了，就可以close了
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseHeader(SocketInputStream input) 
		throws IOException, ServletException{
		while(true) {
			HttpHeader header = new HttpHeader();
			input.readHeader(header);	//填充header对象
			if(header.nameEnd == 0) {
				if(header.valueEnd == 0) {
					return;
				}else {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.colon"));
				}
			}
			String name = new String(header.name, 0, header.nameEnd);
			String value = new String(header.value, 0, header.valueEnd);
			this.request.addHeader(name, value);
			//额外处理特殊的header
			if(Objects.equals(name, "cookie")) {
				Cookie[] cookies = RequestUtil.parseCookieHeader(value);
				for(Cookie cookie : cookies) {
					if(Objects.equals(cookie.getName(), "jsessionid")) {
						if(!this.request.isRequestedSessionIdFromCookie()) {
							this.request.setRequestedSessionId(cookie.getValue());
							this.request.setRequestedSessionCookie(true);
							this.request.setRequestedSessionURL(false);
						}
					}
					this.request.addCookie(cookie);
				}
			}else if(Objects.equals(name, "content-length")) {
				int n = Integer.parseInt(value);
				this.request.setContentLength(n);
			}
		}
	}
	
	private void parseRequeset(SocketInputStream input, OutputStream output) 
		throws IOException, ServletException {
		
		//填充requestLine对象，相当于初步自动解析
		input.readRequestLine(this.requestLine);
		String method = new String(requestLine.method, 0, requestLine.methodEnd);
		String uri;
		String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
		//检测重要字段是否缺失
		if(method.length() < 1) {
			throw new ServletException("Missing HTTP request method");
		}else if(requestLine.uriEnd < 1) {
			throw new ServletException("Missing HTTP request URI");
		}
		//寻找uri中是否有查询参数
		int question = this.requestLine.indexOf("?");
		if(question >= 0) {
			this.request.setQueryString(new String(requestLine.uri, question+1, 
					requestLine.uriEnd - question-1));
			//uri到问号为止，不包括查询参数
			uri = new String(requestLine.uri, 0, question);
		}else {
			this.request.setQueryString(null);
			uri = new String(requestLine.uri, 0, requestLine.uriEnd);
		}
		//继续检查uri是否带协议之类的前缀
		if(!uri.startsWith("/")) {
			int pos = uri.indexOf("://");
			if(pos != -1) {
				//说明有http://www.abc.com这样的域名，需要尝试找下一个/
				pos = uri.indexOf('/', pos+3);
				if(pos == -1) {
					uri = "";
				}else {
					uri = uri.substring(pos);
				}
			}
		}
		
		//尝试从uri中解析会话ID之类（古老的URL重写技术）
		String match = ";jsessionid=";
		int semicolon = uri.indexOf(match);
		if(semicolon != -1) {
			String rest = uri.substring(semicolon + match.length());
			int semicolon2 = rest.indexOf(';');
			//如果后面还有内容，则还属于uri的内容
			if(semicolon2 >= 0) {
				this.request.setRequestedSessionId(rest.substring(0, semicolon2));
				rest = rest.substring(semicolon2);
			}else {
				this.request.setRequestedSessionId(rest);
				rest = "";
			}
			this.request.setRequestedSessionURL(true);
			uri = uri.substring(0, semicolon) + rest;
		}else {
			this.request.setRequestedSessionId(null);
			this.request.setRequestedSessionURL(false);
		}
		
		String normalizedUri = normalize(uri);
		
		this.request.setMethod(method);
		this.request.setProtocol(protocol);
		if(normalizedUri != null) {
			this.request.setRequestURI(normalizedUri);
		}else {
			this.request.setRequestURI(uri);
		}
		
		if(normalizedUri == null) {
			throw new ServletException("Invalid URI: " + uri + "'");
		}
	}
	
	protected String normalize(String path) {
		if(path == null) return null;
		
		String normalized = path;
		
		 // Normalize "/%7E" and "/%7e" at the beginning to "/~"
	    if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
	      normalized = "/~" + normalized.substring(4);

	    // Prevent encoding '%', '/', '.' and '\', which are special reserved
	    // characters
	    if ((normalized.indexOf("%25") >= 0)
	      || (normalized.indexOf("%2F") >= 0)
	      || (normalized.indexOf("%2E") >= 0)
	      || (normalized.indexOf("%5C") >= 0)
	      || (normalized.indexOf("%2f") >= 0)
	      || (normalized.indexOf("%2e") >= 0)
	      || (normalized.indexOf("%5c") >= 0)) {
	      return null;
	    }

	    if (normalized.equals("/."))
	      return "/";

	    // Normalize the slashes and add leading slash if necessary
	    if (normalized.indexOf('\\') >= 0)
	      normalized = normalized.replace('\\', '/');
	    if (!normalized.startsWith("/"))
	      normalized = "/" + normalized;

	    // Resolve occurrences of "//" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("//");
	      if (index < 0)
	        break;
	      normalized = normalized.substring(0, index) +
	        normalized.substring(index + 1);
	    }

	    // Resolve occurrences of "/./" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("/./");
	      if (index < 0)
	        break;
	      normalized = normalized.substring(0, index) +
	        normalized.substring(index + 2);
	    }

	    // Resolve occurrences of "/../" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("/../");
	      if (index < 0)
	        break;
	      if (index == 0)
	        return (null);  // Trying to go outside our context
	      int index2 = normalized.lastIndexOf('/', index - 1);
	      normalized = normalized.substring(0, index2) +
	        normalized.substring(index + 3);
	    }

	    // Declare occurrences of "/..." (three or more dots) to be invalid
	    // (on some Windows platforms this walks the directory tree!!!)
	    if (normalized.indexOf("/...") >= 0)
	      return (null);

	    // Return the normalized path that we have completed
	    return (normalized);
	}
}
