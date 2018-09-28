package ex07.pyrmont.core;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.InstanceListener;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Logger;
import org.apache.catalina.Manager;
import org.apache.catalina.Mapper;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.LifecycleSupport;

public class SimpleWrapper implements Wrapper, Pipeline, Lifecycle {

	private Servlet instance = null;
	private String servletClass;
	private Loader loader;
	private String name;
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	private SimplePipeline pipeline = new SimplePipeline(this);
	protected Container parent = null;
	protected boolean started = false;
	
	
	public SimpleWrapper() {
		this.pipeline.setBasic(new SimpleWrapperValve());
	}
	
	@Override
	public synchronized void addValve(Valve valve) {
		this.pipeline.addValve(valve);
	}
	
	@Override
	public Servlet allocate() throws ServletException {
		
		if(this.instance == null) {
			instance = this.loadServlet();
		}
		return this.instance;
	}
	
	private Servlet loadServlet() throws ServletException{
		
		if(this.instance != null) return instance;
		
		String actualClass = this.servletClass;
		
		Loader loader = this.getLoader();
		if(loader == null) throw new ServletException("No loader.");
	
		ClassLoader classLoader = loader.getClassLoader();
		Class<?> classClass = null;
		try {
			classClass = classLoader.loadClass(actualClass);
		} catch (ClassNotFoundException e) {
			throw new ServletException("Servlet class not found");
		}
		Servlet servlet = null;
		try {
			servlet = (Servlet)classClass.newInstance();
		} catch (Exception e) {
			throw new ServletException("Failed to instantiate servlet");
		}
		
		//在这里调用init方法
		servlet.init(null);
		return servlet;
	}
	
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Loader getLoader() {
		if(this.loader != null) {
			return this.loader;
		}
		if(this.parent != null) {
			return this.parent.getLoader();
		}
		return null;
	}

	@Override
	public void setLoader(Loader loader) {
		this.loader = loader;
	}
	
	@Override
	public String getServletClass() {
		return this.servletClass;
	}

	@Override
	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Container getParent() {
		return this.parent;
	}

	@Override
	public void setParent(Container container) {
		this.parent = container;
	}
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		//委托给pipeline来先执行各个valve
		this.pipeline.invoke(request, response);
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
		System.out.println("Starting Wrapper " + this.name);
		if(this.started) {
			throw new LifecycleException("Wrapper already started");
		}
		this.lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
		this.started = true;
		
		//启动各个组件，没有子容器了，所有只有loader和pipeline
		if(this.loader != null && this.loader instanceof Lifecycle) {
			((Lifecycle)this.loader).start();
		}
		//启动pipeline
		if(this.pipeline instanceof Lifecycle) {
			((Lifecycle)this.pipeline).start();
		}
		//都完事了
		this.lifecycle.fireLifecycleEvent(START_EVENT, null);	
		this.lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
	}

	@Override
	public void stop() throws LifecycleException {
		
		System.out.println("Stopping wrapper " + this.name);
		
		//要负责清理servlet，以完成3个方法
		this.instance.destroy();
		this.instance = null;
		
		if(!this.started) {
			throw new LifecycleException("Wrapper " + name + " not started");
		}
		
		//得人工触发
		this.lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
		this.lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		this.started = false;
	
		//关闭各个组件
		//关闭pipeline
		if(this.pipeline instanceof Lifecycle) {
			((Lifecycle)this.pipeline).stop();
		}
		//关闭loader
		if(this.loader != null && this.loader instanceof Lifecycle) {
			((Lifecycle)this.loader).stop();
		}
		//都完事了
		this.lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
	}
	
	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogger(Logger logger) {
		// TODO Auto-generated method stub

	}

	@Override
	public Manager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setManager(Manager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public Cluster getCluster() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCluster(Cluster cluster) {
		// TODO Auto-generated method stub

	}

	@Override
	public ClassLoader getParentClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public Realm getRealm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRealm(Realm realm) {
		// TODO Auto-generated method stub

	}

	@Override
	public DirContext getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResources(DirContext resources) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addChild(Container child) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addContainerListener(ContainerListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMapper(Mapper mapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Container findChild(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Container[] findChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContainerListener[] findContainerListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapper findMapper(String protocol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapper[] findMappers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Container map(Request request, boolean update) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeChild(Container child) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeContainerListener(ContainerListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMapper(Mapper mapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Valve getBasic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBasic(Valve valve) {
		// TODO Auto-generated method stub

	}

	@Override
	public Valve[] getValves() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeValve(Valve valve) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getAvailable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAvailable(long available) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getJspFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJspFile(String jspFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoadOnStartup() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLoadOnStartup(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRunAs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRunAs(String runAs) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUnavailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addInitParameter(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInstanceListener(InstanceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSecurityReference(String name, String link) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deallocate(Servlet servlet) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public String findInitParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] findInitParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findSecurityReference(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] findSecurityReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load() throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeInitParameter(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeInstanceListener(InstanceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSecurityReference(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unavailable(UnavailableException unavailable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() throws ServletException {
		// TODO Auto-generated method stub

	}
}
