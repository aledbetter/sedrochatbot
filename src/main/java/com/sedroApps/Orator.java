package main.java.com.sedroApps;

import java.util.HashMap;
import java.util.List;

public class Orator {
	Sedro processor = null;
	ChatService service = null;
	ChatServer server = null;
	UserAccount user = null;
	String ctok = null;
	
	Orator(ChatServer server, ChatService service, Sedro processor, UserAccount user) {
		this.processor = processor;
		this.service = service;
		this.server = server;
		this.user = user;
	}
	
	public void close() {
		List<HashMap<String, Object>> rmsg = processor.chatBye();
	}
	
	//////////////////////////////////////////////////////
	// process the conversation
	public void process() {
		int procCnt = 0;
		// wake the persona
		if (processor.getStatus().equals("wake")) {
			List<HashMap<String, Object>> msg = processor.chatWake(server.sedro_access_key, 
					user.getSedroPersona(service.getName()), 
					user.getCBUsername(), ctok, null, null, null, -1);
			procCnt++;
		}
			
		List<String> dml = service.getDirectMessages();
		if (dml != null) {
			for (String msg:dml) {
				System.out.println("MSG: " + msg);
				// private direct messages
				// FIXME
				procCnt++;
				List<HashMap<String, Object>> rmsg = processor.chatMsg(msg);
				if (rmsg != null) {
					// FIXME
				}
			}
			
		}
		
		List<String> tml = service.getTimeLine();
		if (tml != null) {
			for (String msg:tml) {
				boolean isDirect = false;
				procCnt++;
				// public direct messages
				if (isDirect) {
				// FIXME
					System.out.println("TLDM: " + msg);

				} else {
				// public board
				// FIXME
					System.out.println("TLMSG: " + msg);

				}
			}
		}
		if (procCnt == 0) {
			List<HashMap<String, Object>> rmsg = processor.chatPoll();
			if (rmsg != null) {
				// FIXME
			}

		}
	}
	
}
