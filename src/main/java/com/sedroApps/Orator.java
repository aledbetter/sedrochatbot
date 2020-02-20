package main.java.com.sedroApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.com.sedroApps.util.Sutil;

public class Orator {
	private List<Sedro> processors = null;
	
	private ChatAdapter service = null;
	private SCServer server = null;
	private UserAccount user = null;
	
	private Sedro processorPublic = null;
	boolean procPoll = false;
	
	Orator(SCServer server, ChatAdapter service, UserAccount user, boolean readPublic, boolean respPublic) {
		this.service = service;
		this.server = server;
		this.user = user;
		String persona = user.getSedroPersona(service.getName());
		if (persona == null) {
			System.out.println("ERROR OProc: NO PERSONA");
			return;
		}
		if (readPublic) {
			// add porcessor for public
			processorPublic = addProcessor(readPublic, respPublic, false);
			processorPublic.setChannel_type(service.getChannel_type());
			processorPublic.setLanguage(service.getLanguage());
			processorPublic.setContext(service.getContext());
			processorPublic.setPersona(user.getSedroPersona(service.getName()));
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
	
	public Sedro findProcessor(String caller_handle) {
		if (getProcessorCount() > 0) {
			for (Sedro s:processors) {
				// close all processors
				if (Sutil.compare(s.getCaller_handle(), caller_handle)) return s;
			}
		}
		return null;
	}
	
	//////////////////////////////////////////////////////
	// process the conversation
	public void process() {
		// this where we look for new calls 
		if (service.isSession_per_direct()) {
			List<HashMap<String, String>> newCalls = service.getDirectCall(this);
			if (newCalls != null && newCalls.size() > 0) {	
				for (HashMap<String, String> call:newCalls) {
					// ADD NEW channels...Processors
					Sedro proc = addProcessor(false, false, true);
					// add information to it
					proc.setChannel_type(service.getChannel_type());
					proc.setLanguage(service.getLanguage());
					proc.setContext(service.getContext());
					proc.setPersona(user.getSedroPersona(service.getName()));
	
					// information specific to this caller
					proc.setCaller_token(call.get("caller_token"));
					proc.setCaller(call.get("caller"));
					proc.setCaller_handle(call.get("caller_handle"));
					
					// what to do with other info?
					proc.setCall_info(call);
				}
			}
		}
		
		if (getProcessorCount() > 0) {
			for (Sedro s:processors) {
				process(s);
			}
		}	
	}
	
	
	private void process(Sedro processor) {
		int procCnt = 0;

		System.out.println("OProc1["+processor.getStatus()+"]["+processor.getPersona()+"]");

		List<HashMap<String, Object>> wake_msg = null;
				
		//////////////////////////////////////////////////////
		// wake the persona
		if (processor.getStatus().equals("wake")) {
			wake_msg = processor.chatWake(server.getSedro_access_key());
			procCnt++;
			System.out.println("OProc2["+processor.getStatus()+"]["+processor.getPersona()+"] msg: " + processor.getMsgNumber());

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
							service.postMessage(processor, resp_msg);
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
				for (HashMap<String, Object> msg:wake_msg) {
					String smsg = (String) msg.get("msg");
					if (smsg == null || smsg.equals("null")) continue;
					service.sendDirectMessage(processor, processor.getCaller_handle(), smsg);
				}
			}

			// Deal with direct messages
			List<HashMap<String, String>> dml = service.getDirectMessages(this);
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
							service.sendDirectMessage(processor, from, msg);
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
		
		System.out.println("OProc3["+processor.getStatus()+"]["+processor.getPersona()+"] msg: " + processor.getMsgNumber());

	}
	
}
