package main.java.com.sedroApps;

import java.util.HashMap;
import java.util.List;

public class Orator {
	Sedro processor = null;
	ChatAdapter service = null;
	SCServer server = null;
	UserAccount user = null;
	String ctok = null;
	
	Orator(SCServer server, ChatAdapter service, Sedro processor, UserAccount user) {
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
		String persona = user.getSedroPersona(service.getName());
		if (persona == null) {
			System.out.println("OProc["+processor.getStatus()+"] NO PERSONA");
			return;
		}
		System.out.println("OProc1["+processor.getStatus()+"]["+persona+"]");

		// wake the persona
		if (processor.getStatus().equals("wake")) {
			List<HashMap<String, Object>> msg = processor.chatWake(server.getSedro_access_key(), 
									persona, user.getCBUsername(), ctok, null, null, null, -1);
			procCnt++;
			System.out.println("OProc2["+processor.getStatus()+"]["+persona+"] msg: " + processor.msg_num);

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
					System.out.println("PUB_D: " + msg);

				} else {
				// public board
				// FIXME
					System.out.println("PUB: " + msg);

				}
			}
		}
		if (procCnt == 0) {
			List<HashMap<String, Object>> rmsg = processor.chatPoll();
			if (rmsg != null) {
				// FIXME
			}

		}
		System.out.println("OProc3["+processor.getStatus()+"]["+persona+"] msg: " + processor.msg_num);

	}
	
}
