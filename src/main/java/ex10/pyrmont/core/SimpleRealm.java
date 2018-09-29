package ex10.pyrmont.core;

import java.beans.PropertyChangeListener;
import java.security.Principal;
import java.security.cert.X509Certificate;

import org.apache.catalina.Container;
import org.apache.catalina.Realm;

public class SimpleRealm implements Realm {

	@Override
	public Container getContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContainer(Container container) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Principal authenticate(String username, String credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal authenticate(String username, byte[] credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal authenticate(String username, String digest, String nonce, String nc, String cnonce, String qop,
			String realm, String md5a2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal authenticate(X509Certificate[] certs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRole(Principal principal, String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

}
