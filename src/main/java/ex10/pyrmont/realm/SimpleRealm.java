package ex10.pyrmont.realm;

import java.beans.PropertyChangeListener;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.catalina.Container;
import org.apache.catalina.Realm;
import org.apache.catalina.realm.GenericPrincipal;

public class SimpleRealm implements Realm {

	private Container container;
	private List<User> users = new ArrayList<>();
	
	public SimpleRealm() {
		this.createUserDatabase();
	}
	
	class User{
		public String username;
		public String password;
		public List<String> roles = new ArrayList<>();
		public User(String username, String password) {
			this.username = username;
			this.password = password;
		}
		public void addRole(String role) {
			this.roles.add(role);
		}
		public List<String> getRoles(){
			return this.roles;
		}
	}
	
	private void createUserDatabase() {
		User user1 = new User("ken", "blackcomb");
		user1.addRole("manager");
		user1.addRole("programmer");
		User user2 = new User("cindy", "bamboo");
		user2.addRole("programmer");
	
		this.users.add(user1);
		this.users.add(user2);
	}
	
	private User getUser(String username, String password) {
		for(User user : this.users) {
			if(Objects.equals(user.username, username) &&
					Objects.equals(user.password, password)) {
				return user;
			}
		}
		return null;
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
	public String getInfo() {
		return "A simple Realm implementation";
	}
	
	@Override
	public Principal authenticate(String username, String credentials) {
		System.out.println("SimpleRealm.authenticate()");
		if(username == null || credentials == null) return null;
		User user = this.getUser(username, credentials);
		if(user == null) return null;
		return new GenericPrincipal(this, user.username, user.password, user.getRoles());
	}
	
	@Override
	public boolean hasRole(Principal principal, String role) {
		if(principal == null || role == null || !(principal instanceof GenericPrincipal)) {
			return false;
		}
		GenericPrincipal gp = (GenericPrincipal)principal;
		if(gp.getRealm() != this) return false;
		return gp.hasRole(role);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

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
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

}
