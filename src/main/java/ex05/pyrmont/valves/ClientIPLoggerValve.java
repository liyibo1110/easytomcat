package ex05.pyrmont.valves;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

public class ClientIPLoggerValve implements Valve, Contained {

	protected Container container;
	
	@Override
	public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
		//先调用后面的valve，这点比较特殊，等于是valves由后往前执行，而且是basic先执行
		valveContext.invokeNext(request, response);
		System.out.println("Client IP Logger Valve");
		ServletRequest sreq = request.getRequest();
		System.out.println(sreq.getRemoteAddr());
		System.out.println("------------------------------------");
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
