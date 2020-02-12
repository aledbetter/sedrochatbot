package main.java.com.sedroApps;

import java.util.List;

public class UserAccount {
	String username;
	
	String consumer_key;
	String consumer_secret;
	String access_token;
	String access_token_secret;
	
	List<ChatService> services = null;
	
	
	public void load() {
		// load user info from DB
		// FIXME
	}
	public void save() {
		// save user info to DB
		// FIXME
	}
	
}
