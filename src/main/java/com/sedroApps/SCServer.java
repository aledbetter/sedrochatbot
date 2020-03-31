package main.java.com.sedroApps;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class SCServer {
	private List<SCTenant> tenants = null;
	
	
	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(12);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);
		return(hashed_password);
	}
	public static boolean checkPassword(String password_plaintext, String stored_hash) {
		boolean password_verified = false;
	//	if(null == stored_hash || !stored_hash.startsWith("$2a$"))
	//		throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
		password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

		return(password_verified);
	}
	public static SCTenant login(String username, String password) {
		if (username == null || password == null) return null;
		synchronized (username) {
			// FIXME make global
			SCTenant sc = SCTenant.getChatServer();
			if (!username.equals(sc.getUsername())) return null;
			if (checkPassword(password, sc.getPassword())) {
				return sc;
			}
		}
		return null;
	}
}
