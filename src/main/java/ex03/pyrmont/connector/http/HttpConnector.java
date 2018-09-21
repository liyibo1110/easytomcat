package ex03.pyrmont.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConnector implements Runnable {

	boolean stopped;
	private String scheme = "http";	//当前协议
	
	public String getScheme() {
		return scheme;
	}
	
	@Override
	public void run() {
		ServerSocket serverSocket = null;
		int port = 18080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while(!stopped) {
			//阻塞等待连接
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				continue;	//继续下一轮阻塞
			}
			//将自身构造HttpProcessor实例，并调用process方法
			HttpProcessor processor = new HttpProcessor(this);
			processor.process(socket);
		}
	}

	//总入口，当做一个线程来执行
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
}
