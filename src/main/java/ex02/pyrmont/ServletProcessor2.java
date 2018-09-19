package ex02.pyrmont;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor2 {

	public void process(Request request, Response response) {
		
		String uri = request.getUri();	///servlet/aaaServlet
		String servletName = uri.substring(uri.lastIndexOf("/")+1);
		URLClassLoader loader = null;
		
		//创建URLClassLoader
		try {
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classpath = new File(Constants.WEB_ROOT);	//把webroot目录当做classpath
			String repository = (new URL("file", null, classpath.getCanonicalPath() + File.separator)).toString();	//必须是目录，否则会从jar里加载
			urls[0] = new URL(null, repository, streamHandler);
			loader = new URLClassLoader(urls);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Class<?> myClass = null;
		try {
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Servlet servlet = null;
		RequestFacade requestFacade = new RequestFacade(request);
		ResponseFacade responseFacade = new ResponseFacade(response);
		
		try {
			servlet = (Servlet)myClass.newInstance();
			//实际调用
			servlet.service((ServletRequest)requestFacade, (ServletResponse)responseFacade);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
