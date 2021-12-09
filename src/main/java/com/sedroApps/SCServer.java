package com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.sedroApps.adapter.ChatAdapter;
import com.sedroApps.util.DButil;

public class SCServer {
	private static SCServer serv = null;
	private static List<SCTenant> tenants = null;

	
	public SCServer() {
		serv = this;
		
	    // make a base Tenant: hack kinda
		SCTenant tenant = new SCTenant();
	    tenant.setId(DButil.SINGLE_KEY);
	    tenant.setUsername("admin");
	    tenant.setDefaults();
	    tenant.save();
	    
	    tenants = new ArrayList<>();
	    
	    // load
	    load();
	}
	
	public static SCServer getServer() {
		return serv;
	}

	
	/////////////////////////////////////////////////////
	// TENANTS
	public void addTenant(SCTenant tenant) {
		synchronized (tenants) {
			if (!tenants.contains(tenant)) tenants.add(tenant);
		}
	}
	public void removeTenant(SCTenant tenant) {
		synchronized (tenants) {
			if (!tenants.contains(tenant)) tenants.remove(tenant);
		}
	}
	// get tenant by ID
	public static SCTenant getTenant(String tenant_id) {
		synchronized (tenants) {
			for (SCTenant t:tenants) {
				if (t.getId().equals(tenant_id)) return t;
			}
			return null;
		}
	}
	public static SCTenant findTenantApi_key(String api_key) {
		synchronized (tenants) {
			for (SCTenant t:tenants) {
				if (t.getSedro_access_key() == null) continue;
				if (t.getSedro_access_key().equals(api_key)) return t;
			}
			return null;
		}
	}
	// join new tenant
	public static SCTenant newTenant(String api_key) {
		synchronized (tenants) {
			SCTenant t = findTenantApi_key(api_key);
			if (t != null) {
				System.out.println("WARN existing tenant join[" + t.getId()+"] " + t.getUsername());
				return null; // have it
			}
			t = new SCTenant();
			t.setDefaults();
			if (t.setup(api_key)) {
				t.save();
				t.init();
				getServer().addTenant(t);
				return t;
			}
		}
		return null;
	}
	
	
	/////////////////////////////////////////////////////
	// PASSOWORDS
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
		synchronized (tenants) {
			for (SCTenant t:tenants) {
				if (!username.equals(t.getUsername())) continue;
				if (checkPassword(password, t.getPassword())) return t;
				return null;
			}
		}
		return null;
	}
	
	
	/////////////////////////////////////////////////////
	// UTILS	
	public ChatAdapter findChatService(String id) {
		synchronized (tenants) {
			for (SCTenant t:tenants) {
				ChatAdapter ca = t.findChatService(id);
				if (ca != null) return ca;
			}
			return null;
		}
	}
	public SCCall findCallByID(String id) {
		synchronized (tenants) {
			for (SCTenant t:tenants) {
				SCCall c = t.findCallByID(id);
				if (c != null) return c;
			}
			return null;
		}
	}
	
	
	/////////////////////////////////////////////////////
	// DB Load all Tenants
	public void load() {
		// Load the data
	    List<HashMap<String, Object>>  tl = DButil.loadAll();
	    if (tl == null || tl.size() < 1) return;
	    System.out.println("Tenants loading: " + tl.size());
		synchronized (tenants) {
		    for (HashMap<String, Object> sm:tl) {
		    	SCTenant t = null;
		    	String tid = (String)sm.get("id");
		    	if (tid.equals(DButil.SINGLE_KEY)) continue;
		    	
		    	String username = (String)sm.get("username");
		    	t = getTenant(tid);
		    	if (t == null) t = new SCTenant();
			    System.out.println("  TID[" + tid + "] username: " + username);
				//  load from the data map
				synchronized (t) {
					t.load(sm);
					t.init();
					addTenant(t);
				}
		    }
	    }
	}
}
