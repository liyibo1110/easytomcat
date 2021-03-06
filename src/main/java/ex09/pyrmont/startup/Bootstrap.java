package ex09.pyrmont.startup;

import java.io.IOException;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.session.StandardManager;
import org.apache.naming.resources.ProxyDirContext;

import ex06.pyrmont.core.SimpleWrapper;
import ex08.pyrmont.core.SimpleContextConfig;

public class Bootstrap {

	public static void main(String[] args) {
		System.setProperty("catalina.base", System.getProperty("user.dir"));
		Connector connector = new HttpConnector();
		Wrapper wrapper1 = new SimpleWrapper();
		wrapper1.setName("Session");
		wrapper1.setServletClass("SessionServlet");	
		
		Context context = new StandardContext();
		context.setPath("/myApp");
		context.setDocBase("myApp");
		context.addChild(wrapper1);
		context.addServletMapping("/myApp/Session", "Session");
		
		LifecycleListener listener = new SimpleContextConfig();
		((Lifecycle)context).addLifecycleListener(listener);
		
		Loader loader = new WebappLoader();
		context.setLoader(loader);
		
		connector.setContainer(context);
		
		//增加Manager
		Manager manager = new StandardManager();
		context.setManager(manager);
		
		//启动
		try {
			connector.initialize();
			((Lifecycle)connector).start();
			((Lifecycle)context).start();
			//随便按个键，就停止应用
			System.in.read();
			((Lifecycle)context).stop();
		} catch (LifecycleException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
