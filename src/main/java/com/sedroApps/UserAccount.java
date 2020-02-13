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
	
	////////////////////////////////////////
	// Manage the services
	public ChatService findChatService(String service) {
		if (services == null) return null;
		for (ChatService cs:services) {
			if (cs.getName().equals(service)) return cs;
		}
		return null;
	}
	public void addChatService(ChatService cs) {
		if (services == null) services = new ArrayList<>();
		if (!services.contains(cs)) services.add(cs);
	}
	public void removeChatService(String service) {
		if (services == null) return;
		ChatService cs = findChatService(service);
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
	public Orator findOratorForChatService(ChatService cs) {
		if (orators == null || cs == null) return null;
		for (Orator orat:orators) {
			if (orat.service.equals(cs)) return orat;
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
		System.out.println("Process User: " + this.getCBUsername());

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
				ChatService cs = findChatService(key);
				switch (key) {
				case "twitter":
					if (cs == null) cs = new ChatTwitter();
					if (cs.init(this) == 0) {
						addChatService(cs);
						Orator orat = new Orator(ChatServer.getChatServer(), cs, new Sedro(), this);
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
		ChatServer.getChatServer().save();
	}
	
	public void load(HashMap<String, Object> um) {
		// load user info from DB
		if (service_info == null) service_info = new HashMap<>();
		if (services == null) services = new ArrayList<>();
		
		// Load 
// FIXME
		
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
		return m;
	}
	
}
