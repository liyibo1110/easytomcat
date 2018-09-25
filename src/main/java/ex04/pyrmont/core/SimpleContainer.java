package ex04.pyrmont.core;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Logger;
import org.apache.catalina.Manager;
import org.apache.catalina.Mapper;
import org.apache.catalina.Realm;
import org.apache.catalina.Request;
import org.apache.catalina.Response;

public class SimpleContainer implements Container {

	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
	
	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public Loader getLoader() {
		return null;
	}

	@Override
	public void setLoader(Loader loader) {

	}

	@Override
	public Logger getLogger() {
		return null;
	}

	@Override
	public void setLogger(Logger logger) {

	}

	@Override
	public Manager getManager() {
		return null;
	}

	@Override
	public void setManager(Manager manager) {

	}

	@Override
	public Cluster getCluster() {
		return null;
	}

	@Override
	public void setCluster(Cluster cluster) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public Container getParent() {
		return null;
	}

	@Override
	public void setParent(Container container) {

	}

	@Override
	public ClassLoader getParentClassLoader() {
		return null;
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {

	}

	@Override
	public Realm getRealm() {
		return null;
	}

	@Override
	public void setRealm(Realm realm) {

	}

	@Override
	public DirContext getResources() {
		return null;
	}

	@Override
	public void setResources(DirContext resources) {

	}

	@Override
	public void addChild(Container child) {

	}

	@Override
	public void addContainerListener(ContainerListener listener) {

	}

	@Override
	public void addMapper(Mapper mapper) {

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public Container findChild(String name) {
		return null;
	}

	@Override
	public Container[] findChildren() {
		return null;
	}

	@Override
	public ContainerListener[] findContainerListeners() {
		return null;
	}

	@Override
	public Mapper findMapper(String protocol) {
		return null;
	}

	@Override
	public Mapper[] findMappers() {
		return null;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		String servletName = ((HttpServletRequest)request).getRequestURI();
		//获取要加载的servlet的文件名
		servletName = servletName.substring(servletName.lastIndexOf("/")+1);
			
		URL[] urls = new URL[1];
		URLStreamHandler streamHandler = null;
		File classpath = new File(WEB_ROOT);
		String repository = (new URL("file", null, classpath.getCanonicalPath() + File.separator)).toString();
		urls[0] = new URL(null, repository, streamHandler);
		URLClassLoader loader = new URLClassLoader(urls);
		Class<?> myClass = null;
		try {
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Servlet servlet = null;
		try {
			servlet = (Servlet)myClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Container map(Request request, boolean update) {
		return null;
	}

	@Override
	public void removeChild(Container child) {

	}

	@Override
	public void removeContainerListener(ContainerListener listener) {

	}

	@Override
	public void removeMapper(Mapper mapper) {

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

}