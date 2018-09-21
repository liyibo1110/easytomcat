package ex03.pyrmont.connector.http;

import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {
	
	private HttpConnector connector;
	private HttpRequest request;
	private HttpResponse response;
	
	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}
	
	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;
		
		input = new SocketInputStream(socket.getInputStream(), 2048);
		output = socket.getOutputStream();
		//构造request和response，每个process只有单一的request和response
		request = new HttpRequest(input);
		response = new HttpResponse(output);
		response.setRequest(request);
	}
}
