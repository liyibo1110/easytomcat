package ex03.pyrmont.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletException;

import org.apache.catalina.util.StringManager;

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
		} catch (IOException e) {
			e.printStackTrace();
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
	}
}
