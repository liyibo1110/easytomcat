package ex14.pyrmont.startup;

import java.io.IOException;

import javax.swing.JInternalFrame;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.loader.WebappLoader;

import ex11.pyrmont.core.SimpleContextConfig;

public class Bootstrap {

	public static void main(String[] args) {
		System.setProperty("catalina.base", System.getProperty("user.dir"));
		Connector connector = new HttpConnector();
		
		Wrapper wrapper1 = new StandardWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("PrimitiveServlet");	
		Wrapper wrapper2 = new StandardWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("ModernServlet");
		
		Context context = new StandardContext();
		context.setPath("/app1");
		context.setDocBase("app1");
		LifecycleListener listener = new SimpleContextConfig();
		((Lifecycle)context).addLifecycleListener(listener);
		
		context.addChild(wrapper1);
		context.addChild(wrapper2);
		
		Host host = new StandardHost();
		host.addChild(context);
		host.setName("localhost");
		host.setAppBase("webapps");
		
		Loader loader = new WebappLoader();
		context.setLoader(loader);
		
		Engine engine = new StandardEngine();
		engine.addChild(host);
		engine.setDefaultHost("localhost");
		
		Service service = new StandardService();
		service.setName("Stand-alone Service");
		Server server = new StandardServer();
		server.addService(service);
		service.addConnector(connector);
		
		context.addServletMapping("/Primitive", "Primitive");
		context.addServletMapping("/Modern", "Modern");
		
		connector.setContainer(engine);
		
		//启动
		if(server instanceof Lifecycle) {
			try {
				//只需要启动server即可
				server.initialize();
				((Lifecycle)server).start();
				server.await();
			} catch (LifecycleException e) {
				e.printStackTrace();
			}
		}
		//await返回，就关闭
		if(server instanceof Lifecycle) {
			try {
				//只需要启动server即可
				((Lifecycle)server).stop();
			} catch (LifecycleException e) {
				e.printStackTrace();
			}
		}
		
	}
}
