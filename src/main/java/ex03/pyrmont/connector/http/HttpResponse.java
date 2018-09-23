package ex03.pyrmont.connector.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.util.CookieTools;

import ex03.pyrmont.connector.ResponseStream;
import ex03.pyrmont.connector.ResponseWriter;

public class HttpResponse implements HttpServletResponse {

	private static final int BUFFER_SIZE = 1024;
	HttpRequest request;
	OutputStream output;
	PrintWriter writer;
	
	protected byte[] buffer = new byte[BUFFER_SIZE];
	protected int bufferCount = 0;
	
	/**
	 * response已经被提交？
	 */
	protected boolean committed = false;
	/**
	 * 写入时实际的字节数
	 */
	protected int contentCount = 0;
	/**
	 * 返回的内容长度
	 */
	protected int contentLength = -1;
	/**
	 * 返回的MIME类型
	 */
	protected String contentType = null;
	/**
	 * 返回时相关的编码信息
	 */
	protected String encoding = null;
	
	protected List<Cookie> cookies = new ArrayList<>();
	protected Map<String, List<String>> headers = new HashMap<>();
	
	protected final SimpleDateFormat format = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	
	protected String message = this.getStatusMessage(HttpServletResponse.SC_OK);
	
	protected int status = HttpServletResponse.SC_OK;
	
	public HttpResponse(OutputStream output) {
		this.output = output;
	}
	
	public void finishResponse() {
		if(this.writer != null) {
			writer.flush();
			writer.close();
		}
	}
	
	public int getContentLength() {
		return this.contentLength;
	}
	
	protected String getProtocol() {
		return this.request.getProtocol();
	}
	
	protected String getStatusMessage(int status) {
	    switch (status) {
	      case SC_OK:
	        return ("OK");
	      case SC_ACCEPTED:
	        return ("Accepted");
	      case SC_BAD_GATEWAY:
	        return ("Bad Gateway");
	      case SC_BAD_REQUEST:
	        return ("Bad Request");
	      case SC_CONFLICT:
	        return ("Conflict");
	      case SC_CONTINUE:
	        return ("Continue");
	      case SC_CREATED:
	        return ("Created");
	      case SC_EXPECTATION_FAILED:
	        return ("Expectation Failed");
	      case SC_FORBIDDEN:
	        return ("Forbidden");
	      case SC_GATEWAY_TIMEOUT:
	        return ("Gateway Timeout");
	      case SC_GONE:
	        return ("Gone");
	      case SC_HTTP_VERSION_NOT_SUPPORTED:
	        return ("HTTP Version Not Supported");
	      case SC_INTERNAL_SERVER_ERROR:
	        return ("Internal Server Error");
	      case SC_LENGTH_REQUIRED:
	        return ("Length Required");
	      case SC_METHOD_NOT_ALLOWED:
	        return ("Method Not Allowed");
	      case SC_MOVED_PERMANENTLY:
	        return ("Moved Permanently");
	      case SC_MOVED_TEMPORARILY:
	        return ("Moved Temporarily");
	      case SC_MULTIPLE_CHOICES:
	        return ("Multiple Choices");
	      case SC_NO_CONTENT:
	        return ("No Content");
	      case SC_NON_AUTHORITATIVE_INFORMATION:
	        return ("Non-Authoritative Information");
	      case SC_NOT_ACCEPTABLE:
	        return ("Not Acceptable");
	      case SC_NOT_FOUND:
	        return ("Not Found");
	      case SC_NOT_IMPLEMENTED:
	        return ("Not Implemented");
	      case SC_NOT_MODIFIED:
	        return ("Not Modified");
	      case SC_PARTIAL_CONTENT:
	        return ("Partial Content");
	      case SC_PAYMENT_REQUIRED:
	        return ("Payment Required");
	      case SC_PRECONDITION_FAILED:
	        return ("Precondition Failed");
	      case SC_PROXY_AUTHENTICATION_REQUIRED:
	        return ("Proxy Authentication Required");
	      case SC_REQUEST_ENTITY_TOO_LARGE:
	        return ("Request Entity Too Large");
	      case SC_REQUEST_TIMEOUT:
	        return ("Request Timeout");
	      case SC_REQUEST_URI_TOO_LONG:
	        return ("Request URI Too Long");
	      case SC_REQUESTED_RANGE_NOT_SATISFIABLE:
	        return ("Requested Range Not Satisfiable");
	      case SC_RESET_CONTENT:
	        return ("Reset Content");
	      case SC_SEE_OTHER:
	        return ("See Other");
	      case SC_SERVICE_UNAVAILABLE:
	        return ("Service Unavailable");
	      case SC_SWITCHING_PROTOCOLS:
	        return ("Switching Protocols");
	      case SC_UNAUTHORIZED:
	        return ("Unauthorized");
	      case SC_UNSUPPORTED_MEDIA_TYPE:
	        return ("Unsupported Media Type");
	      case SC_USE_PROXY:
	        return ("Use Proxy");
	      case 207:       // WebDAV
	        return ("Multi-Status");
	      case 422:       // WebDAV
	        return ("Unprocessable Entity");
	      case 423:       // WebDAV
	        return ("Locked");
	      case 507:       // WebDAV
	        return ("Insufficient Storage");
	      default:
	        return ("HTTP Response Status " + status);
	    }
	}
	
	public OutputStream getStream() {
		return this.output;
	}
	
	protected void sendHeader() throws IOException{
		if(isCommitted()) return;
		
		OutputStreamWriter osr = null;
		osr = new OutputStreamWriter(this.getStream(), this.getCharacterEncoding());
		final PrintWriter outputWriter = new PrintWriter(osr);
		//发送Status头部
		outputWriter.print(this.getProtocol());
		outputWriter.print(" ");
		outputWriter.print(this.status);
		if(this.message != null) {
			outputWriter.print(" ");
			outputWriter.print(this.message);
		}
		outputWriter.print("\r\n");
		//发送content-length和content-type
		if(this.getContentType() != null) {
			outputWriter.print("Content-Type: " + this.getContentType() + "\r\n");
		}
		if(this.getContentLength() >= 0) {
			outputWriter.print("Content-Length: " + this.getContentLength() + "\r\n");
		}
		//发送其余头部
		synchronized (this.headers) {
			for(String name : this.headers.keySet()) {
				List<String> values = this.headers.get(name);
				for(String value : values) {
					outputWriter.print(name);
					outputWriter.print(": ");
					outputWriter.print(value);
					outputWriter.print("\r\n");
				}
			}
		}
		//发送cookie
		synchronized (this.cookies) {
			for(Cookie cookie : this.cookies) {
				outputWriter.print(CookieTools.getCookieHeaderName(cookie));
				outputWriter.print(": ");
				outputWriter.print(CookieTools.getCookieHeaderValue(cookie));
				outputWriter.print("\r\n");
			}
		}
		//发送结束标志
		outputWriter.print("\r\n");
		outputWriter.flush();
		this.committed = true;
	}
	
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
	
	public void sendStaticResource() throws IOException{
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		
		File file = new File(Constants.WEB_ROOT, this.request.getRequestURI());
		try {
			if(file.exists()) {
				//读取文件
				fis = new FileInputStream(file);
				//尝试输出头部（type是写暂时死的，如果不写头，只有IE可以渲染）
				String headerMessage = "HTTP/1.1 200 OK\r\n" + 
				"Content-Type: text/html\r\n" +
				"Content-Length: " + fis.available() + "\r\n" +
				"\r\n";
				output.write(headerMessage.getBytes());
				int ch = fis.read(bytes, 0, BUFFER_SIZE);
				//System.out.println("ch:" + ch);
				while(ch != -1) { //重复直到为-1说明读完了	
					output.write(bytes, 0, ch);
					ch = fis.read(bytes, 0, BUFFER_SIZE);	//还是0，不需要计算偏移
				}
			}else {	//文件不存在则返回404错误，格式写死
				String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + 
						"Content-Type: text/html\r\n" + 
						"Content-Length: 23\r\n" +
						"\r\n" +
						"<h1>File Not Found</h1>";
				output.write(errorMessage.getBytes());
			}
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) fis.close();
		}
	}
	
	public void write(int b) throws IOException{
		if(this.bufferCount >= this.buffer.length) {
			flushBuffer();
		}
		this.buffer[this.bufferCount++] = (byte)b;
	}
	
	public void write(byte b[]) throws IOException{
		write(b, 0, b.length);
	}
	
	public void write(byte b[], int off, int len) throws IOException{
		if(len == 0) return;
		//buffer还有足够空间装下len长度，则直接复制进去就完事了
		if(len <= (this.buffer.length - this.bufferCount)) {
			System.arraycopy(b, off, this.buffer, this.bufferCount, len);
			this.bufferCount += len;
			this.contentCount += len;
			return;
		}
		//buffer不够，则先输出清空
		flushBuffer();
		//发几次整块的
		int iterations = len / this.buffer.length;
		//最后剩余的起始位置
		int leftoverStart = iterations * this.buffer.length;
		//最后剩余的长度
		int leftoverLeft = len - leftoverStart;
		//先发送整块的
		for(int i = 0; i < iterations; i++) {
			write(b, off + (i * this.buffer.length), this.buffer.length);
		}
		//最后发送一次剩余的
		if(leftoverLeft > 0) {
			write(b, off + leftoverStart, leftoverLeft);
		}
	}
	
	@Override
	public void flushBuffer() throws IOException {
		if(this.bufferCount > 0) {
			this.output.write(this.buffer, 0, this.bufferCount);
		}
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		if(this.encoding == null) {
			return "ISO-8859-1";
		}else {
			return this.encoding;
		}
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		ResponseStream newStream = new ResponseStream(this);
		newStream.setCommit(false);
		OutputStreamWriter osr = new OutputStreamWriter(newStream, this.getCharacterEncoding());
		this.writer = new ResponseWriter(osr);
		return this.writer;
	}

	@Override
	public boolean isCommitted() {
		return (this.committed);
	}

	@Override
	public void reset() {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public void setBufferSize(int arg0) {

	}

	@Override
	public void setContentLength(int length) {
		if(isCommitted()) return;
		this.contentLength = length;
	}

	@Override
	public void setContentType(String type) {
		if(isCommitted()) return;
		this.contentType = type;
	}
	
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public void setLocale(Locale locale) {
		if(isCommitted()) return;
		
		String language = locale.getLanguage();
		if(language != null && language.length() > 0) {
			String country = locale.getCountry();
			StringBuilder value = new StringBuilder(language);
			if(country != null && country.length() > 0) {
				value.append('-');
				value.append(country);
			}
			setHeader("Content-Language", value.toString());
		}
	}

	@Override
	public void addCookie(Cookie cookie) {
		if(isCommitted()) return;
		
		synchronized (cookies) {
			this.cookies.add(cookie);
		}
	}

	@Override
	public void addDateHeader(String name, long value) {
		if(isCommitted()) return;
		
		addHeader(name, this.format.format(new Date(value)));
	}

	@Override
	public void addHeader(String name, String value) {
		if(isCommitted()) return;
		
		List<String> values = new ArrayList<>();
		values.add(value);
		synchronized (this.headers) {
			this.headers.put(name, values);
		}
		//额外判断name是不是content-length或content-type
		String match = name.toLowerCase();
		if(Objects.equals(match, "content-length")) {
			int contentLength = Integer.parseInt(value);
			if(contentLength >= 0) setContentLength(contentLength);
		}else if(Objects.equals(match, "content-type")) {
			setContentType(value);
		}
	}

	@Override
	public void addIntHeader(String name, int value) {
		if(isCommitted()) return;
		addHeader(name, ""+value);
	}

	@Override
	public boolean containsHeader(String name) {
		synchronized (this.headers) {
			return (this.headers.get(name) != null);
		}
	}

	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	@Override
	public String encodeURL(String url) {
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
	public void sendError(int arg0) throws IOException {

	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {

	}

	@Override
	public void sendRedirect(String arg0) throws IOException {

	}

	@Override
	public void setDateHeader(String name, long value) {
		if(isCommitted()) return;
		setHeader(name, this.format.format(new Date(value)));
	}

	@Override
	public void setHeader(String name, String value) {
		if(isCommitted()) return;
		
		List<String> values = new ArrayList<>();
		values.add(value);
		
		//覆盖旧的List
		synchronized (this.headers) {
			headers.put(name, values);
		}
		String match = name.toLowerCase();
		if(Objects.equals(match, "content-length")) {
			int contentLength = Integer.parseInt(value);
			if(contentLength >= 0) {
				setContentLength(contentLength);
			}
		}else if(Objects.equals(match, "content-type")) {
			setContentType(value);
		}
	}

	@Override
	public void setIntHeader(String name, int value) {
		if(isCommitted()) return;
		setHeader(name, ""+value);
	}

	@Override
	public void setStatus(int arg0) {

	}

	@Override
	public void setStatus(int arg0, String arg1) {

	}

}
