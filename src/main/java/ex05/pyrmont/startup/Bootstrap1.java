package ex05.pyrmont.startup;

import java.io.IOException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

import ex05.pyrmont.core.SimpleLoader;
import ex05.pyrmont.core.SimpleWrapper;
import ex05.pyrmont.valves.ClientIPLoggerValve;
import ex05.pyrmont.valves.HeaderLoggerValve;

public class Bootstrap1 {

	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		Wrapper wrapper = new SimpleWrapper();
		wrapper.setServletClass("ModernServlet");	//写死了
		
		Loader loader = new SimpleLoader();
		Valve valve1 = new HeaderLoggerValve();
		Valve valve2 = new ClientIPLoggerValve();
		
		wrapper.setLoader(loader);
		((Pipeline)wrapper).addValve(valve1);
		((Pipeline)wrapper).addValve(valve2);
		
		connector.setContainer(wrapper);
		
		//启动
		try {
			connector.initialize();
			connector.start();
			//随便按个键，就停止应用
			System.in.read();
		} catch (LifecycleException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
