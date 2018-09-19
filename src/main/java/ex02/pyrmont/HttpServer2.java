package ex02.pyrmont;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer2 {
	
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	
	private boolean shutdown = false;
	//静态文件存储目录
	public static final String WEB_ROOT = 
			System.getProperty("user.dir") + File.separator + "webroot";
	
	
	public static void main(String[] args) {
		HttpServer2 server = new HttpServer2();
		System.out.println("server started..");
		server.await();
		System.out.println("shutdown...");
	}
	
	public void await() {
		ServerSocket serverSocket = null;
		int port = 18080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//socket没问题，则开始循环监听
		while(!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				//创建Request对象
				Request request = new Request(input);
				request.parse();
				//创建Response对象
				Response response = new Response(output);
				response.setRequest(request);
				
				//根据请求路径，来决定静态处理还是servlet处理
				if(request.getUri().startsWith("/servlet")) {
					ServletProcessor2 processor = new ServletProcessor2();
					processor.process(request, response);
				}else {
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(request, response);
				}
				
				//关闭客户端socket
				socket.close();
				//检查是否传来的数据是要关闭服务器
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	
}
