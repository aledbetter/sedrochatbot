package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAccount {
	// chatbot info (name may be different accross services)
	String username;
	
	// service info
	HashMap<String, HashMap<String, String>> service_info = null;
	List<ChatService> services = null;
	List<SedroInstance> sedros = null; // FIXME one per service Or accross... 

	
	UserAccount(String username) {
		this.username = username;
	}
		
	public String getCBUsername() {
		return username;
	}
	
	public String getUsername(String service) {
		return getServiceInfo(service, "username");
	}
	public String getServiceInfo(String service, String element) {
		if (service_info == null) return null;
		HashMap<String, String> hm = service_info.get(service);
		if (hm == null) return null;		
		return hm.get(element);
	}
	
	
	public void load() {
		// load user info from DB
		if (service_info == null) service_info = new HashMap<>();
		if (services == null) services = new ArrayList<>();
		
		// Load 
// FIXME

		// initiallize all the interfaces
		for (String key:service_info.keySet()) {
			switch (key) {
			case "twitter":
				ChatService cs = new ChatTwitter();
				cs.init(this);
				break;
			case "facebook":
				//ChatService cs = new ChatTwitter();
				//cs.init(this);
				break;
			case "slack":
				//ChatService cs = new ChatTwitter();
				//cs.init(this);
				break;
			case "phone":
				//ChatService cs = new ChatTwitter();
				//cs.init(this);
				break;
			}
		}
		
	}
	
	public void save() {
		// save user info to DB
		if (service_info == null || service_info.keySet().size() < 1) return;
		for (String key:service_info.keySet()) {
			// save this info...
			// FIXME

		}
	}
	
	// get al lthe user info as a map..
	public HashMap<String, Object> getMap() {
		HashMap<String, Object> m = new HashMap<>();
		m.put("username", this.username);
		if (service_info != null && service_info.keySet().size() > 0) {
			List<HashMap<String, Object>> sl = new ArrayList<>();
			for (String key:service_info.keySet()) {
				HashMap<String, String> sconfig = service_info.get(key);
				HashMap<String, Object> sm = new HashMap<>();
				sm.put("service", key);
				for (String param:sconfig.keySet()) {
					sm.put(param, sconfig.get(param));
				}
				sl.add(sm);
			}
			m.put("services", sl);
		}
		return m;
	}
	
}
