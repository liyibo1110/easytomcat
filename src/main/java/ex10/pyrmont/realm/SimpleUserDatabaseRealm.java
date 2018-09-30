package ex10.pyrmont.realm;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.users.MemoryUserDatabase;

public class SimpleUserDatabaseRealm extends RealmBase {

	protected UserDatabase database = null;
	protected static final String name = "SimpleUserDatabaseRealm";
	
	protected String resourceName = "UserDatabase";
	
	@Override
	public Principal authenticate(String username, String credentials) {
		User user = database.findUser(username);
		if(user == null) return null;
		//验证密码
		boolean validated = false;
		if(hasMessageDigest()) {
			validated = digest(credentials).equalsIgnoreCase(user.getPassword());
		}else {
			validated = digest(credentials).equals(user.getPassword());
		}
		if(!validated) return null;
		
		List<String> combined = new ArrayList<>();	//不重复的rolename
		Iterator<Role> roles = user.getRoles();
		while(roles.hasNext()) {
			Role role = roles.next();
			String rolename = role.getRolename();
			if(!combined.contains(rolename)) {
				combined.add(rolename);
			}
		}
		Iterator<Group> groups = user.getGroups();
		while(groups.hasNext()) {
			Group group = groups.next();
			roles = group.getRoles();
			while(roles.hasNext()) {
				Role role = roles.next();
				String rolename = role.getRolename();
				if(!combined.contains(rolename)) {
					combined.add(rolename);
				}
			}
		}
		
		return new GenericPrincipal(this, user.getUsername(), user.getPassword(), combined);
	}
	
	public void createDatabase(String path) {
		this.database = new MemoryUserDatabase(name);
		((MemoryUserDatabase)database).setPathname(path);
		try {
			database.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasRole(Principal principal, String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getName() {
		return this.name;
	}

	@Override
	protected String getPassword(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Principal getPrincipal(String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
