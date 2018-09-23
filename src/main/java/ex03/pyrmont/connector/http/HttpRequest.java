package ex03.pyrmont.connector.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.util.Enumerator;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.RequestUtil;

import ex03.pyrmont.connector.RequestStream;

public class HttpRequest implements HttpServletRequest {

	private String contentType;
	private int contentLength;
	private InetAddress inetAddress;
	private InputStream input;
	private String method;
	
	private String protocol;
	private String queryString;
	private String requestURI;
	private String serverName;
	private int serverPort;
	
	private Socket socket;
	private boolean requestedSessionCookie;
	private String requestedSessionId;
	private boolean requestedSessionURL;
	
	/**
	 * request附带的数据对象容器
	 */
	protected Map<String, Object> attributes = new HashMap<>();
	/**
	 * authorization认证相关信息
	 */
	protected String authorization = null;
	/**
	 * context上下文相关路径
	 */
	protected String contextPath = "";
	protected List<Cookie> cookies = new ArrayList<>();
	
	
	protected static List<String> empty = new ArrayList<>();
	
	/**
	 * 参数parameter是否被parse
	 */
	protected boolean parsed = false;
	protected String pathInfo = null;
	
	/**
	 * getReader方法返回的对象
	 */
	protected BufferedReader reader = null;
	/**
	 * getInputStream()方法返回的对象
	 */
	protected ServletInputStream stream = null;
	
	protected SimpleDateFormat formats[] = {
		    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
		    new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
		    new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
	};
	
	protected Map<String, List<String>> headers = new HashMap<>();
	protected ParameterMap parameters = null;
	
	public HttpRequest(InputStream input) {
		this.input = input;
	}
	
	protected void parseParameter() {
		if(this.parsed) return;
		
		ParameterMap results = this.parameters;
		if(results == null) {
			results = new ParameterMap();
		}
		results.setLocked(false);
		String encoding = this.getCharacterEncoding();
		if(encoding == null) encoding = "ISO-8859-1";
		
		String queryString = this.getQueryString();
		
		//解析queryString，填充到parameter里
		try {
			RequestUtil.parseParameters(results, queryString, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//解析content-type
		String contentType = this.getContentType();
		if(contentType == null) contentType = "";
		int semicolon = contentType.indexOf(';');
		if(semicolon >= 0) {
			contentType = contentType.substring(0, semicolon).trim();
		}else {
			contentType = contentType.trim();
		}
		
		//解析POST类型的额外信息
		if(Objects.equals(this.getMethod(), "POST") &&
				(this.getContentLength() > 0) && 
				Objects.equals(contentType, "application/x-www-form-urlencoded")) {
			try {
				int max = getContentLength();
				int len = 0;
				byte buf[] = new byte[this.getContentLength()];
				ServletInputStream is = this.getInputStream();
				while(len < max) {
					int next = is.read(buf, len, max-len);
					if(next < 0) break;
					len += next;
				}
				is.close();
				if(len < max) {
					throw new RuntimeException("content length mismatch");
				}
				RequestUtil.parseParameters(results, buf, encoding);
			} catch (UnsupportedEncodingException e) {
				//什么也不做
			} catch (IOException e) {
				throw new RuntimeException("content read fail");
			}
		}
		
		results.setLocked(true);
		this.parsed = true;
		this.parameters = results;
	}
	
	public ServletInputStream createInputStream() throws IOException {
		return new RequestStream(this);
	}
	
	public InputStream getStream() {
		return this.input;
	}
	
	public void setContentLength(int length) {
		this.contentLength = length;
	}
	
	public void setContentType(String type) {
		this.contentType = type;
	}
	
	public void setInet(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}
	
	public void setContextPath(String path) {
		if(path == null) {
			this.contextPath = "";
		}else {
			this.contextPath = path;
		}
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setPathInfo(String path) {
		this.pathInfo = path;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	
	public void setServerName(String name) {
		this.serverName = name;
	}
	
	public void setServerPort(int port) {
		this.serverPort = port;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void setRequestedSessionCookie(boolean flag) {
		this.requestedSessionCookie = flag;
	}

	public void setRequestedSessionId(String requestedSessionId) {
		this.requestedSessionId = requestedSessionId;
	}

	public void setRequestedSessionURL(boolean flag) {
		requestedSessionURL = flag;
	}

	@Override
	public Object getAttribute(String name) {
		synchronized (this.attributes) {
			return this.attributes.get(name);
		}
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		synchronized (this.attributes) {
			return new Enumerator(this.attributes.keySet());
		}
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		
		if(this.reader != null) {
			throw new IllegalArgumentException("getInputStream has been called");
		}
		if(this.stream == null) {
			this.stream = createInputStream();
		}
		return this.stream;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return null;
	}

	@Override
	public String getParameter(String name) {
		this.parseParameter();
		String values[] = (String[])this.parameters.get(name);
		if(values != null) {
			return values[0];
		}else {
			return null;
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		this.parseParameter();
		return this.parameters;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		this.parseParameter();
		return new Enumerator(this.parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		this.parseParameter();
		String values[] = (String[])this.parameters.get(name);
		if(values != null) {
			return values;
		}else {
			return null;
		}
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if(this.stream != null) {
			throw new IllegalStateException("getInputStream has been called.");
		}
		if(this.reader == null) {
			String encoding = this.getCharacterEncoding();
			if(encoding == null) {
				encoding = "ISO-8859-1";
			}
			InputStreamReader isr = new InputStreamReader(this.createInputStream(), encoding);
			reader = new BufferedReader(isr);
		}
		return this.reader;
	}

	@Override
	public String getRealPath(String arg0) {
		return null;
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public String getRemoteHost() {
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {

	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {

	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public String getContextPath() {
		return this.contextPath;
	}

	public void addCookie(Cookie cookie) {
		synchronized (this.cookies) {
			this.cookies.add(cookie);
		}
	}
	
	@Override
	public Cookie[] getCookies() {
		synchronized (this.cookies) {
			if(this.cookies.size() < 1) return null;
			Cookie[] results = new Cookie[cookies.size()];
			return (Cookie[])cookies.toArray(results);
		}
	}

	@Override
	public long getDateHeader(String name) {
		String value = getHeader(name);
		if(value == null) return -1L;
		
		for(SimpleDateFormat sdf : this.formats) {
			try {
				Date date = sdf.parse(value);
				return date.getTime();
			} catch (ParseException e) {
				//什么也不做
			}
		}
		
		throw new IllegalArgumentException(value);
	}

	public void addHeader(String name, String value) {
		name = name.toLowerCase();
		List<String> values = this.headers.get(name);
		if(values == null) {
			values = new ArrayList<>();
			this.headers.put(name, values);
		}
		values.add(value);
	}
	
	@Override
	public String getHeader(String name) {
		name = name.toLowerCase();
		synchronized (this.headers) {
			List<String> values = headers.get(name);
			if(values != null) {
				return values.get(0);
			}else {
				return null;
			}
		}
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		synchronized (this.headers) {
			return new Enumerator(this.headers.keySet());
		}
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		name = name.toLowerCase();
		synchronized (this.headers) {
			List<String> values = this.headers.get(name);
			if(values != null) {
				return new Enumerator(values);
			}else {
				return new Enumerator(empty);
			}
		}
	}

	@Override
	public int getIntHeader(String name) {
		String value = getHeader(name);
		if(value == null) {
			return -1;
		}else {
			return Integer.parseInt(value);
		}
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getPathInfo() {
		return this.pathInfo;
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	@Override
	public String getQueryString() {
		return this.queryString;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	@Override
	public StringBuffer getRequestURL() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		return this.requestedSessionId;
	}

	@Override
	public String getServletPath() {
		return null;
	}

	@Override
	public HttpSession getSession() {
		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return this.isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return false;
	}
}
