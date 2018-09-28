import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class PrimitiveServlet implements Servlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("init");
	}
	
	@Override
	public void service(ServletRequest request, ServletResponse response) 
						throws ServletException, IOException {
		System.out.println("from service");
		PrintWriter out = response.getWriter();
		out.println("Hello. Roses are red.");
		out.print("Violets are blue");
		out.flush();
	}
	
	@Override
	public void destroy() {
		System.out.println("destory");
	}

	@Override
	public String getServletInfo() {
		return null;
	}
	
	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

}
