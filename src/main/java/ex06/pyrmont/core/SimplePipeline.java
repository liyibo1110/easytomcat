package ex06.pyrmont.core;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

public class SimplePipeline implements Pipeline, Lifecycle {

	protected Valve basic = null;
	protected Container container = null;
	protected Valve[] valves = new Valve[0];
	
	public SimplePipeline(Container container) {
		this.setContainer(container);
	}
	
	public void setContainer(Container container) {
		this.container = container;
	}
	
	@Override
	public Valve getBasic() {
		return this.basic;
	}

	@Override
	public void setBasic(Valve valve) {
		this.basic = valve;
		((Contained)valve).setContainer(this.container);
	}

	@Override
	public void addValve(Valve valve) {
		if(valve instanceof Contained) {
			((Contained)valve).setContainer(this.container);
		}
		
		synchronized (this.valves) {
			Valve results[] = new Valve[this.valves.length+1];
			System.arraycopy(this.valves, 0, results, 0, valves.length);
			results[valves.length] = valve;
			this.valves = results;
		}
	}

	@Override
	public Valve[] getValves() {
		return this.valves;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		new SimplePipelineValveContext().invokeNext(request, response);
	}

	@Override
	public void removeValve(Valve valve) {

	}

	protected class SimplePipelineValveContext implements ValveContext{
		
		protected int stage = 0;
		
		@Override
		public String getInfo() {
			return null;
		}
		
		@Override
		public void invokeNext(Request request, Response response)
					throws IOException, ServletException{
			int subscript = stage;
			stage++;
			//执行valves里面的业务，在valve内部再次调用invokeNext，利用第3个this
			if(subscript < valves.length) {
				valves[subscript].invoke(request, response, this);
			}else if(subscript == valves.length && basic != null) {
				basic.invoke(request, response, this);
			}else {
				throw new ServletException("No valve");
			}
		}
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() throws LifecycleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() throws LifecycleException {
		// TODO Auto-generated method stub
		
	}
	
}
