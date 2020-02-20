package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAccount {
	// chatbot info (name may be different accross services)
	private String username;
	
	// service info
	HashMap<String, HashMap<String, String>> service_info = null;
	HashMap<String, HashMap<String, String>> service_state = null;
	List<ChatAdapter> services = null;
	List<Orator> orators = null;
	
	
	UserAccount(String username) {
		this.username = username;
	}
		
	public String getCBUsername() {
		return username;
	}
	
	////////////////////////////////////////
	// Manage configuration
	public String getUsername(String service) {
		return getServiceInfo(service, "username");
	}
	public String getCaller_token(String service) {
		return getServiceInfo(service, "caller_token");
	}
	public String getSedroPersona(String service) {
		return getServiceInfo(service, "sedro_persona");
	}		
	public String getServiceInfo(String service, String element) {
		if (service_info == null) return null;
		HashMap<String, String> hm = service_info.get(service);
		if (hm == null) return null;		
		return hm.get(element);
	}
	public void setServiceInfo(String service, String element, String value) {
		if (service == null || element == null) return;
		if (service_info == null) service_info = new HashMap<>();

		HashMap<String, String> hm = service_info.get(service);
		if (hm == null) {
			hm = new HashMap<>();
			hm.put("service", service);
		}
		if (value.isEmpty()) {
			hm.remove(element);
		} else {
			hm.put(element, value);
		}
		service_info.put(service, hm);
	}
	
	public String getServiceState(String service, String element) {
		if (service_state == null) return null;
		HashMap<String, String> hm = service_state.get(service);
		if (hm == null) return null;		
		return hm.get(element);
	}
	public void setServiceState(String service, String element, String value) {
		if (service == null || element == null) return;
		if (service_state == null) service_state = new HashMap<>();

		HashMap<String, String> hm = service_state.get(service);
		if (hm == null) {
			hm = new HashMap<>();
			hm.put("service", service);
		}
		if (value.isEmpty()) {
			hm.remove(element);
		} else {
			hm.put(element, value);
		}
		service_state.put(service, hm);
	}
	
	////////////////////////////////////////
	// Manage the services
	public ChatAdapter findChatService(String service) {
		if (services == null) return null;
		for (ChatAdapter cs:services) {
			if (cs.getName().equals(service)) return cs;
		}
		return null;
	}
	public void addChatService(ChatAdapter cs) {
		if (services == null) services = new ArrayList<>();
		if (!services.contains(cs)) services.add(cs);
	}
	public void removeChatService(String service) {
		if (services == null) return;
		ChatAdapter cs = findChatService(service);
		if (cs == null) return;
		cs.disconnnect(this);
		// remove any orator associated
		removeOrator(findOratorForChatService(cs));
		services.remove(cs);
	}
	
	
	////////////////////////////////////////
	// Manage Orators
	public void addOrator(Orator orator) {
		if (orators == null) orators = new ArrayList<>();
		if (!orators.contains(orator)) orators.add(orator);
	}
	
	public Orator findOratorForChatService(ChatAdapter cs) {
		if (orators == null || cs == null) return null;
		for (Orator orat:orators) {
			if (orat.getChatService().equals(cs)) return orat;
		}
		return null;
	}
	public void removeOrator(Orator orat) {
		if (orators == null || orat == null) return;
		orat.close();
		orators.remove(orat);
	}

	
	
	//////////////////////////////////////////////////////
	// process the Ortors
	public void process() {
		// process the orators
		if (orators != null && orators.size() > 0) {
			for (Orator orat:orators) {
				orat.process();
			}
		}
	}
	
		
	//////////////////////////////////////////////////////
	// initialize OR Reinitialize all the servies
	public void initializeServices() {
		if (service_info.keySet().size() > 0) {
			// initiallize all the interfaces
			for (String key:service_info.keySet()) {
				ChatAdapter cs = findChatService(key);
				switch (key) {
				case "twitter":
					if (cs == null) cs = new ChatTwitter();
					if (cs.init(this) == 0) {
						addChatService(cs);
						Orator orat = new Orator(SCServer.getChatServer(), cs, this, true, false);
						this.addOrator(orat);
					} else {
						removeChatService(key);
					}

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

	}
	
	////////////////////////////////////////
	// Functionality
	public void save() {
		// always save all
		SCServer.getChatServer().save();
	}
	
	public void load(HashMap<String, Object> um) {
		// load user info from DB
		if (service_info == null) service_info = new HashMap<>();
		if (service_state == null) service_state = new HashMap<>();
		if (services == null) services = new ArrayList<>();
		System.out.println("  LOAD: " + um.keySet());

		List<HashMap<String, Object>> sl = (List<HashMap<String, Object>>)um.get("services");
		if (sl != null && sl.size() > 0) {
			for (HashMap<String, Object> hm:sl) {
				String sn = (String)hm.get("service");
				HashMap<String, String> nhm = new HashMap<>();
				for (String key:hm.keySet()) {
					if (key.equals("service")) continue;
					System.out.println("      S["+sn+"]["+key+"]: " + hm.get(key));

					nhm.put(key, ""+hm.get(key));
				}
				service_info.put(sn, nhm);
			}
		}
		sl = (List<HashMap<String, Object>>)um.get("services_state");
		if (sl != null && sl.size() > 0) {
			for (HashMap<String, Object> hm:sl) {
				String sn = (String)hm.get("service");
				HashMap<String, String> nhm = new HashMap<>();
				for (String key:hm.keySet()) {
					if (key.equals("service")) continue;
					nhm.put(key,""+hm.get(key));
				}
				service_state.put(sn, nhm);
			}
		}
		// initialize Services
		initializeServices();
	}

	// get al lthe user info as a map..
	public HashMap<String, Object> getMap() {
		HashMap<String, Object> m = new HashMap<>();
		m.put("username", this.username);
		if (service_info != null && service_info.keySet().size() > 0) {
			List<HashMap<String, Object>> sl = new ArrayList<>();
			for (String key:service_info.keySet()) {
				//System.out.println("     get["+key+"]");

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
		if (service_state != null && service_state.keySet().size() > 0) {
			List<HashMap<String, Object>> sl = new ArrayList<>();
			for (String key:service_state.keySet()) {
				//System.out.println("     get["+key+"]");

				HashMap<String, String> sconfig = service_state.get(key);
				HashMap<String, Object> sm = new HashMap<>();
				sm.put("service", key);
				for (String param:sconfig.keySet()) {
					sm.put(param, sconfig.get(param));
				}
				sl.add(sm);
			}
			m.put("services_state", sl);
		}
		return m;
	}
	
}
