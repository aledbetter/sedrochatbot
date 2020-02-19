package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orator {
	private List<Sedro> processors = null;
	
	private ChatAdapter service = null;
	private SCServer server = null;
	private UserAccount user = null;
	private String ctok = null;
	
	private Sedro processorPublic = null;
	boolean procPoll = false;
	
	Orator(SCServer server, ChatAdapter service, UserAccount user, boolean readPublic, boolean respPublic) {
		this.service = service;
		this.server = server;
		this.user = user;

		if (readPublic) {
			// add porcessor for public
			processorPublic = addProcessor(readPublic, respPublic, false);
		}
	}
	
	public int getProcessorCount() {
		if (processors == null) return 0;
		return processors.size();
	}
	public ChatAdapter getChatService() {
		return this.service;
	}
	
	// close 
	public void close() {
		if (getProcessorCount() > 0) {
			for (Sedro s:processors) {
				// close all processors
				List<HashMap<String, Object>> msg = s.chatBye();
			}
		}	 
	}
	
	public Sedro addProcessor(boolean readPublic, boolean respPublic, boolean directMsg) {
		Sedro s = new Sedro(readPublic, respPublic, directMsg);
		if (processors == null) processors = new ArrayList<>();
		processors.add(s);
		return s;
	}
	public void removeProcessor(Sedro s) {
		if (processors == null) return;
		processors.remove(s);
	}
	
	//////////////////////////////////////////////////////
	// process the conversation
	public void process() {
		// this where we look for new calls 
		if (service.isSession_per_direct()) {
			// FIXME
//FIXME ADD NEW channels...Processors
		}
		
		if (getProcessorCount() > 0) {
			String persona = user.getSedroPersona(service.getName());
			if (persona == null) {
				System.out.println("OProc: NO PERSONA");
				return;
			}
			for (Sedro s:processors) {
				process(s, persona);
			}
		}	
	}
	
	
	private void process(Sedro processor, String persona) {
		int procCnt = 0;

		System.out.println("OProc1["+processor.getStatus()+"]["+persona+"]");

		List<HashMap<String, Object>> wake_msg = null;
				
		//////////////////////////////////////////////////////
		// wake the persona
		if (processor.getStatus().equals("wake")) {
			wake_msg = processor.chatWake(server.getSedro_access_key(), 
									persona, user.getCBUsername(), ctok, null, null, null, -1);
			procCnt++;
			System.out.println("OProc2["+processor.getStatus()+"]["+persona+"] msg: " + processor.getMsgNumber());

		}
						
		//////////////////////////////////////////////////////
		// PUBLIC MESSAGES
		if (processor.isReadPublic()) {
			List<String> tml = service.getPublicMessages();
			if (tml != null) {
				for (String msg:tml) {
					System.out.println("PUB: " + msg);
					if (!processor.isRespPublic()) continue;
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
		
		
		//////////////////////////////////////////////////////
		// PRIVATE MESSAGES
		if (processor.isDirectMsg()) {
			if (wake_msg != null) {
				// where to send these messages?
				// FIXME
			}
/*
 * FIXME where to add / remove new sessions for callers?			
 */
			// Deal with direct messages
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
		}
		
		//////////////////////////////////////////////////////	
		// poll for new messages to post/send
		if (procPoll && procCnt == 0) {
			List<HashMap<String, Object>> rmsg = processor.chatPoll();
			if (rmsg != null) {
				// where to send these messages?
				// FIXME
			}
		}
		
		System.out.println("OProc3["+processor.getStatus()+"]["+persona+"] msg: " + processor.getMsgNumber());

	}
	
}
