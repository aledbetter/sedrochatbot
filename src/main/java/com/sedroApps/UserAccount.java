 /*************************************************************************
 * Ledbetter CONFIDENTIAL
 * __________________
 * 
 * [2018] - [2020] Aaron Ledbetter
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains
 * the property of Aaron Ledbetter. The intellectual and technical 
 * concepts contained herein are proprietary to Aaron Ledbetter and 
 * may be covered by U.S. and Foreign Patents, patents in process, 
 * and are protected by trade secret or copyright law. 
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Aaron Ledbetter.
 */
package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.com.sedroApps.util.Sutil;

public class UserAccount {
	// chatbot info (name may be different accross services)
	private String username;
	private String sedro_persona;
	
	// service info
	HashMap<String, HashMap<String, String>> service_info = null;
	HashMap<String, HashMap<String, String>> service_state = null;
	List<ChatAdapter> services = null;
	List<Orator> orators = null;
	
	private CbMessage msgcb = null;
	
	
	UserAccount(String username) {
		this.username = username;
	}
		
	public String getCBUsername() {
		return username;
	}
	
	
	public String getSedroPersona() {
		return sedro_persona;
	}		
	public void setSedroPersona(String sedro_persona) {
		this.sedro_persona = sedro_persona;
	}
	
	
	////////////////////////////////////////
	// singleton message calback override for the user.... might register for details ?
	public CbMessage getMessageCb() {
		return msgcb;
	}
	public void setMessageCb(CbMessage msgcb) {
		this.msgcb = msgcb;
	}
	public void setMessageCb(String callback) {
		this.msgcb = SCServer.getChatServer().getCbMsg(callback);
	}	

	
	////////////////////////////////////////
	// Manage configuration
	public String getServiceInfo(String id, String element) {
		if (service_info == null) return null;
		HashMap<String, String> hm = service_info.get(id);
		if (hm == null) return null;		
		return hm.get(element);
	}
	public void setServiceInfo(String id, String element, String value) {
		if (id == null || element == null) return;
		if (service_info == null) service_info = new HashMap<>();

		HashMap<String, String> hm = service_info.get(id);
		if (hm == null) {
			hm = new HashMap<>();
			hm.put("id", id);
		}
		if (value.isEmpty()) {
			hm.remove(element);
		} else {
			hm.put(element, value);
		}
		service_info.put(id, hm);
	}
	
	public String getServiceState(String id, String element) {
		if (service_state == null) return null;
		HashMap<String, String> hm = service_state.get(id);
		if (hm == null) return null;		
		return hm.get(element);
	}
	public void setServiceState(String id, String element, String value) {
		if (id == null || element == null) return;
		if (service_state == null) service_state = new HashMap<>();

		HashMap<String, String> hm = service_state.get(id);
		if (hm == null) {
			hm = new HashMap<>();
			hm.put("id", id);
		}
		if (value.isEmpty()) {
			hm.remove(element);
		} else {
			hm.put(element, value);
		}
		service_state.put(id, hm);
	}
	
	public String addChatService(String service) {
		// new service
		String id = Sutil.getGUIDNoString();
		setServiceInfo(id, "id", id);
		setServiceInfo(id, "service", service);
		return id;
	}
	
	////////////////////////////////////////
	// Manage the services
	public ChatAdapter findChatServiceByService(String service) {
		if (services == null) return null;
		for (ChatAdapter cs:services) {
			if (cs.getName().equals(service)) return cs;
		}
		return null;
	}
	public ChatAdapter findChatService(String id) {
		if (services == null) return null;
		for (ChatAdapter cs:services) {
			if (cs.getId().equals(id)) return cs;
		}
		return null;
	}
	public void addChatService(ChatAdapter cs) {
		if (services == null) services = new ArrayList<>();
		if (!services.contains(cs)) services.add(cs);
	}
	public void removeChatService(String id) {
		if (services == null) return;
		ChatAdapter cs = findChatService(id);
		if (cs == null) return;
		cs.disconnnect(this);
		// remove any orator associated
		removeOrator(findOratorForChatService(cs));
		services.remove(cs);
		if (service_info != null) service_info.remove(id);
		if (service_state != null) service_state.remove(id);
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
			boolean readPublic = true;
			boolean respPublic = true;
		
			// initiallize all the interfaces
			for (String id:service_info.keySet()) {
				ChatAdapter cs = findChatService(id);
				String service = this.getServiceInfo(id, "service");
				switch (service) {
				case "twitter":
					if (cs == null) cs = new ChatTwitter(this, id);
					break;
				case "facebook":
					if (cs == null) cs = new ChatFacebook(this, id);
					break;
				case "whatsapp":
					if (cs == null) cs = new ChatWhatsapp(this, id);
					break;
				case "sms":
					if (cs == null) cs = new ChatSMS(this, id);
					break;
				}
				// up up date
				if (cs.init(this) == 0) {
					addChatService(cs);
					if (!cs.isPublicMsg()) {
						readPublic = respPublic = false;
					}
					Orator orat = new Orator(SCServer.getChatServer(), cs, this, readPublic, respPublic);
					this.addOrator(orat);
				} else {
	// ?
					removeChatService(id);
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
	public void saveState() {
		// always save all
		SCServer.getChatServer().save();
	}
	
	public void load(HashMap<String, Object> um) {
		// user config
		this.sedro_persona = (String)um.get("sedro_persona");
		String cbname = (String)um.get("callback");
		if (cbname != null) this.setMessageCb(cbname);
		
		// load user info from DB
		if (service_info == null) service_info = new HashMap<>();
		if (service_state == null) service_state = new HashMap<>();
		if (services == null) services = new ArrayList<>();
		//System.out.println("  LOAD: " + um.keySet());

		// load Config
		List<HashMap<String, Object>> sl = (List<HashMap<String, Object>>)um.get("services");
		if (sl != null && sl.size() > 0) {
			for (HashMap<String, Object> hm:sl) {
				String sid = (String)hm.get("id");
				HashMap<String, String> nhm = new HashMap<>();
				for (String key:hm.keySet()) {
					//System.out.println("      S["+sn+"]["+key+"]: " + hm.get(key));
					nhm.put(key, ""+hm.get(key));
				}
				service_info.put(sid, nhm);
			}
		}
		
		// load State
		sl = (List<HashMap<String, Object>>)um.get("services_state");
		if (sl != null && sl.size() > 0) {
			for (HashMap<String, Object> hm:sl) {
				String sid = (String)hm.get("id");
				HashMap<String, String> nhm = new HashMap<>();
				for (String key:hm.keySet()) {
					nhm.put(key,""+hm.get(key));
				}
				service_state.put(sid, nhm);
			}
		}
		// initialize Services
		initializeServices();
	}

	// get al lthe user info as a map..
	public List<HashMap<String, Object>> getMapState() {
		if (service_state != null && service_state.keySet().size() > 0) {
			List<HashMap<String, Object>> sl = new ArrayList<>();
			for (String id:service_state.keySet()) {
				//System.out.println("     get["+key+"]");

				HashMap<String, String> sconfig = service_state.get(id);
				HashMap<String, Object> sm = new HashMap<>();
				sm.put("id", id);			
				for (String param:sconfig.keySet()) {
					sm.put(param, sconfig.get(param));
				}
				sl.add(sm);
			}
			return sl;
		}
		return null;
	}
	// get al lthe user info as a map..
	public HashMap<String, Object> getMap() {
		HashMap<String, Object> m = new HashMap<>();
		m.put("username", this.username);
		m.put("sedro_persona", this.sedro_persona);
		if (this.getMessageCb() != null) {
			m.put("callback", this.getMessageCb().getName());
		}
		if (service_info != null && service_info.keySet().size() > 0) {
			List<HashMap<String, Object>> sl = new ArrayList<>();
			for (String id:service_info.keySet()) {
				//System.out.println("     get["+key+"]");
				
				HashMap<String, String> sconfig = service_info.get(id);
				HashMap<String, Object> sm = new HashMap<>();
				sm.put("id", id);
				for (String param:sconfig.keySet()) {
					sm.put(param, sconfig.get(param));
				}
				sl.add(sm);
			}
			m.put("services", sl);
		}
		
		List<HashMap<String, Object>> sl = getMapState();
		if (sl != null) m.put("services_state", sl);
		
		return m;
	}
	
}
