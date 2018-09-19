package ex01.pyrmont;

import java.io.IOException;
import java.io.InputStream;

public class Request {

	private InputStream input;
	private String uri;
	
	public Request(InputStream input) {
		this.input = input;
	}
	
	public void parse() {
		StringBuilder request = new StringBuilder(2048);
		int i;
		byte[] buffer = new byte[2048];	//2k
		try {
			i = input.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			i = -1;
		}
		for(int j = 0; j < i; j++) {
			request.append((char)buffer[j]);
		}
		System.out.println(request.toString());
		this.uri = parseUri(request.toString());
		//System.out.println("uri:" + this.uri);
	}
	
	/**
	 * 截取2个空格中间的部分字符串，用split其实更好
	 * @param requestString
	 * @return
	 */
	private String parseUri(String requestString) {
		int index1, index2;
		index1 = requestString.indexOf(' ');
		if(index1 != -1) {
			index2 = requestString.indexOf(' ', index1+1);
			if(index2 > index1) {
				return requestString.substring(index1+1, index2);
			}
		}
		return null;
	}
	
	public String getUri() {
		return uri;
	}
}
