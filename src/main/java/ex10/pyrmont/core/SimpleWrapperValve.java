package ex10.pyrmont.core;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

public class SimpleWrapperValve implements Valve, Contained {

	//basic valve
	
	protected Container container;
	
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

	@Override
	public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
		//获取container容器
		SimpleWrapper wrapper = (SimpleWrapper)getContainer();
		ServletRequest sreq = request.getRequest();
		ServletResponse sres = response.getResponse();
		
		HttpServletRequest hreq = null;
		if(sreq instanceof HttpServletRequest) hreq = (HttpServletRequest)sreq;
		HttpServletResponse hres = null;
		if(sres instanceof HttpServletResponse) hres = (HttpServletResponse)sres;
	
		//request必须要设置顶层容器context，才能操作session，因为manager在context里
		Context context = (Context)wrapper.getParent();
		request.setContext(context);
		
		//获取Servlet实例
		//System.out.println("调用了allocate");
		Servlet servlet = wrapper.allocate();
		if(hreq != null && hres != null) {
			servlet.service(hreq, hres);
		}else {
			servlet.service(sreq, sres);
		}
	}

}
