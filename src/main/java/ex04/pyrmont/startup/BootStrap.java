package ex04.pyrmont.startup;

import java.io.IOException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.http.HttpConnector;

import ex04.pyrmont.core.SimpleContainer;

public final class BootStrap {

	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		SimpleContainer container = new SimpleContainer();
		connector.setContainer(container);
		
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
