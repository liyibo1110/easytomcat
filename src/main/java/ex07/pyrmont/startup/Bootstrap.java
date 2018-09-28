package ex07.pyrmont.startup;

import java.io.IOException;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.logger.FileLogger;

import ex06.pyrmont.core.SimpleContext;
import ex06.pyrmont.core.SimpleContextLifecycleListener;
import ex06.pyrmont.core.SimpleContextMapper;
import ex06.pyrmont.core.SimpleLoader;
import ex06.pyrmont.core.SimpleWrapper;

public class Bootstrap {

	public static void main(String[] args) {
		Connector connector = new HttpConnector();
		Wrapper wrapper1 = new SimpleWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("PrimitiveServlet");	
		Wrapper wrapper2 = new SimpleWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("ModernServlet");
		
		Context context = new SimpleContext();
		context.addChild(wrapper1);
		context.addChild(wrapper2);
		
		Mapper mapper = new SimpleContextMapper();
		mapper.setProtocol("http");
		context.addMapper(mapper);
		
		LifecycleListener listener = new SimpleContextLifecycleListener();
		((Lifecycle)context).addLifecycleListener(listener);
		
		Loader loader = new SimpleLoader();
		context.setLoader(loader);
		
		context.addServletMapping("/Primitive", "Primitive");
		context.addServletMapping("/Modern", "Modern");
		
		//logger相关
		System.setProperty("catalina.base", System.getProperty("user.dir"));
		FileLogger logger = new FileLogger();
		logger.setPrefix("FileLog_");
		logger.setSuffix(".txt");
		logger.setTimestamp(true);
		logger.setDirectory("webroot");
		context.setLogger(logger);
		
		connector.setContainer(context);
		
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
