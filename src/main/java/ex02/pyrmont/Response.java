package ex02.pyrmont;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import ex02.pyrmont.Request;

public class Response implements ServletResponse {

	private static final int BUFFER_SIZE = 1024;
	private Request request;
	private OutputStream output;
	PrintWriter writer;
	
	public Response(OutputStream output) {
		this.output = output;
	}
	
	public void setRequest(Request request) {
		this.request = request;
	}
	
	public void sendStaticResource() throws IOException{
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		//System.out.println("WEB_ROOT:" + HttpServer.WEB_ROOT);
		File file = new File(Constants.WEB_ROOT, request.getUri());
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
	
	@Override
	public PrintWriter getWriter() throws IOException {
		writer = new PrintWriter(output, true);
		return writer;
	}
	
	@Override
	public void flushBuffer() throws IOException {
		
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
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
	public boolean isCommitted() {
		return false;
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
	public void setContentLength(int arg0) {

	}

	@Override
	public void setContentType(String arg0) {

	}

	@Override
	public void setLocale(Locale arg0) {

	}

}
