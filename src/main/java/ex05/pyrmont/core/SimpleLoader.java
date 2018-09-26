package ex05.pyrmont.core;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import org.apache.catalina.Container;
import org.apache.catalina.DefaultContext;
import org.apache.catalina.Loader;

public class SimpleLoader implements Loader {

	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
	ClassLoader classLoader = null;
	Container container = null;
	
	public SimpleLoader() {
		try {
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classpath = new File(WEB_ROOT);
			String repository = (new URL("file", null, 
								classpath.getAbsolutePath() + File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler);
			classLoader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
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
	public DefaultContext getDefaultContext() {
		return null;
	}

	@Override
	public void setDefaultContext(DefaultContext defaultContext) {

	}

	@Override
	public boolean getDelegate() {
		return false;
	}

	@Override
	public void setDelegate(boolean delegate) {

	}

	@Override
	public String getInfo() {
		return "A simple loader";
	}

	@Override
	public boolean getReloadable() {
		return false;
	}

	@Override
	public void setReloadable(boolean reloadable) {

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public void addRepository(String repository) {

	}

	@Override
	public String[] findRepositories() {
		return null;
	}

	@Override
	public boolean modified() {
		return false;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

}
