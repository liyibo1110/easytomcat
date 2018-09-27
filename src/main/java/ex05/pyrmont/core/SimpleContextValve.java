package ex05.pyrmont.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Wrapper;

public class SimpleContextValve implements Valve, Contained {

	protected Container container;
	
	@Override
	public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
		
		//必须是http的
		if((request.getRequest() instanceof HttpServletRequest) || 
				(response.getResponse() instanceof HttpServletResponse)) {
			return;
		}
		
		//不允许直接访问WEB-INF和META-INF目录
		HttpServletRequest hreq = (HttpServletRequest)request.getRequest();
		String contextPath = hreq.getContextPath();
		String requestURI = ((HttpRequest)request).getDecodedRequestURI();	//现原形了
		//获取相对路径
		String relativeURI = requestURI.substring(contextPath.length()).toLowerCase();
		Context context = (Context)this.getContainer();
		Wrapper wrapper = null;
		try {
			//寻找下层的wrapper容器，可以强转
			wrapper = (Wrapper)context.map(request, true);
		} catch (Exception e) {
			this.badRequest(requestURI, (HttpServletResponse)response.getResponse());
			return;
		}
		if(wrapper == null) {
			this.notFound(requestURI, (HttpServletResponse)response.getResponse());
			return;
		}
		//找到了wrapper
		response.setContext(context);
		wrapper.invoke(request, response);
	}
	
	private void badRequest(String requestURI, HttpServletResponse response) {
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
		} catch (IOException e) {
			//什么也不做
		}
	}
	
	private void notFound(String requestURI, HttpServletResponse response) {
		try {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
		} catch (IOException e) {
			//什么也不做
		}
	}
	
	@Override
	public Container getContainer() {
		return this.container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
