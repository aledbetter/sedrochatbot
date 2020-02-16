package main.java.com.sedroApps;

import java.util.HashMap;
import java.util.List;

public class Orator {
	Sedro processor = null;
	ChatAdapter service = null;
	SCServer server = null;
	UserAccount user = null;
	String ctok = null;
	
	boolean readPublic = false;
	boolean respPublic = false;
	boolean procPoll = false;
	
	Orator(SCServer server, ChatAdapter service, Sedro processor, UserAccount user, boolean readPublic, boolean respPublic) {
		this.processor = processor;
		this.service = service;
		this.server = server;
		this.user = user;
		this.readPublic = readPublic;
		this.respPublic = respPublic;
	}
	
	public void close() {
		List<HashMap<String, Object>> rmsg = processor.chatBye();
	}
	
	//////////////////////////////////////////////////////
	// process the conversation
	public void process() {
		int procCnt = 0;
		String persona = user.getSedroPersona(service.getName());
		if (persona == null) {
			System.out.println("OProc["+processor.getStatus()+"] NO PERSONA");
			return;
		}
		System.out.println("OProc1["+processor.getStatus()+"]["+persona+"]");

		List<HashMap<String, Object>> wake_msg = null;
		
		if (service.isSession_per_direct()) {
			// FIXME this requires big changes
//FIXME
			
		}
		// wake the persona
		if (processor.getStatus().equals("wake")) {
			wake_msg = processor.chatWake(server.getSedro_access_key(), 
									persona, user.getCBUsername(), ctok, null, null, null, -1);
			procCnt++;
			System.out.println("OProc2["+processor.getStatus()+"]["+persona+"] msg: " + processor.msg_num);

		}
		
		if (wake_msg != null) {
			// where to send these messages?
			// FIXME
		}
		
		/*
		msg.num
		msg.msg
		msg.event
		msg.time
		msg.from
		msg.req_base
		msg.rply_type
		msg.qn
		*/			
		List<HashMap<String, String>> dml = service.getDirectMessages();
		if (dml != null) {
			for (HashMap<String, String> mm:dml) {
				String msg = mm.get("msg");
				String from = mm.get("from");
				System.out.println("MSG["+from+"]: " + msg);
				// private direct messages => private direct response
				procCnt++;
				List<HashMap<String, Object>> rmsg = processor.chatMsg(msg);
				if (rmsg != null) {
					if (wake_msg != null) {
						// per user sessions ?
						
						// FIXME
					}
					for (HashMap<String, Object> m:rmsg) {
						// send direct message
						service.sendDirectMessage(from, msg);
					}
				}
			}
			
		}
		if (readPublic) {
			List<String> tml = service.getTimeLine();
			if (tml != null) {
				for (String msg:tml) {
					System.out.println("PUB: " + msg);
					if (!respPublic) continue;
					List<HashMap<String, Object>> rmsg = processor.chatMsg(msg);
					if (rmsg != null) {
						for (HashMap<String, Object> m:rmsg) {
							// send direct message
							String resp_msg = (String)m.get("msg");
							if (resp_msg == null || resp_msg.isEmpty()) continue;
							procCnt++;
							service.postMessage(resp_msg);
							System.out.println("PUB_RESP: " + resp_msg);
						}
					}
				}
			}
		}
		
		if (procPoll && procCnt == 0) {
			List<HashMap<String, Object>> rmsg = processor.chatPoll();
			if (rmsg != null) {
				// where to send these messages?
				// FIXME
			}

		}
		System.out.println("OProc3["+processor.getStatus()+"]["+persona+"] msg: " + processor.msg_num);

	}
	
}
