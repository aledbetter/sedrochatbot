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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import main.java.com.sedroApps.adapter.ChatAdapter;
import main.java.com.sedroApps.api.RestExample;
import main.java.com.sedroApps.msgcb.CbMessage;
import main.java.com.sedroApps.util.Sutil;

public class SCOrator {
	private static final boolean debug = false;
	private static final boolean debug_callinfo = false;
	
		
	// wake with text
	private static final boolean send_timed = true;
	private static final boolean wake_text = false;
	
	
	private List<SCSedro> processors = null;
	
	private ChatAdapter service = null;
	private SCServer server = null;
	private SCUser user = null;
	
	// messages to send to users
	private List<HashMap<String, Object>> msg_set = null;
	private Timer msg_timer = null;

	private SCSedro processorPublic = null;
	boolean procPoll = false;
	
	SCOrator(SCServer server, ChatAdapter service, SCUser user, boolean readPublic, boolean respPublic) {
		this.service = service;
		this.server = server;
		this.user = user;
		String persona = user.getSedroPersona();
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
			processorPublic.setPersona(user.getSedroPersona());
		}
		service.setOrator(this);
		
		msg_set = new ArrayList<>();
		if (send_timed) {
			startSendTimer();
		}
	}
	
	public int getProcessorCount() {
		if (processors == null) return 0;
		return processors.size();
	}
	public ChatAdapter getChatService() {
		return this.service;
	}
	
	// timed send for messages
	private void startSendTimer() {
		if (msg_timer != null) msg_timer.cancel();
		
		msg_timer = new Timer();
		msg_timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
            	// go through messags until time is not up yet
            	List<HashMap<String, Object>> sendList = null;
        		synchronized(msg_set) {       			
        			if (msg_set.size() < 1) return; // nothing to send
        			
        			// make list to send
        			long tm = Sutil.getUTCTime().getTimeInMillis();
        			while (msg_set.size()>0) {
        				HashMap<String, Object> m = msg_set.get(0);
        				Long stm = (Long)m.get("stime");
        				if (stm <= tm) {
        					if (sendList == null) sendList = new ArrayList<>();      					
                			msg_set.remove(0);
                			sendList.add(m);
        				} else {
        					break;
        				}
        			}
        		}
        		if (sendList == null) return; // nothing to send
        		// Send all MESSAGEs
    			for (HashMap<String, Object> m:sendList) {	            	
	            	String txt = (String)m.get("msg");
	            	String to = (String)m.get("to");
	            	String from = (String)m.get("from");
	            	SCSedro processor = (SCSedro)m.get("processor");
	               // System.out.println("TIMER Send("+sendList.size()+")["+service.getName()+"]["+from+"] => ["+to+"]: " + txt);

					service.sendDirectMessage(processor, to, txt);
    			}
            }
        }, 100, 100);		
	}
	
	// close 
	public void close() {
		if (getProcessorCount() > 0) {
			for (SCSedro s:processors) {
				// close all processors
				List<HashMap<String, Object>> msg = s.chatBye();
			}
		}
		if (msg_timer != null) msg_timer.cancel();
	}
	
	public SCSedro addProcessor(boolean readPublic, boolean respPublic, boolean directMsg) {
		SCSedro s = new SCSedro(readPublic, respPublic, directMsg);
		if (processors == null) processors = new ArrayList<>();
		processors.add(s);
		return s;
	}
	public void removeProcessor(SCSedro s) {
		if (processors == null) return;
		processors.remove(s);
	}
	
	public SCSedro findProcessor(String caller_handle) {
		if (getProcessorCount() > 0) {
			for (SCSedro s:processors) {
				// close all processors
				if (Sutil.compare(s.getCaller_handle(), caller_handle)) return s;
			}
		}
		return null;
	}
	
	//////////////////////////////////////////////////////	
	// check callbacks for message override
	private String getFinalMessage(String caname, SCSedro processor, boolean msgPublic, 
			HashMap<String, Object> msgInfo, String msg) {
		if (msg == null || msg.equals("null") || msg.isEmpty()) return null;
		CbMessage cb = this.user.getMessageCb();
		if (cb != null) return cb.getFinalMessage(caname, processor, msgPublic, msgInfo, msg);
		return msg;
	}
	
	//////////////////////////////////////////////////////
	// Add processor for new call
	private SCSedro addNewCall(HashMap<String, String> call) {
		// ADD NEW channels...Processors
		SCSedro proc = addProcessor(false, false, true);
		// add information to it
		proc.setChannel_type(service.getChannel_type());
		proc.setLanguage(service.getLanguage());
		proc.setContext(service.getContext());
		proc.setPersona(user.getSedroPersona());

		// information specific to this caller
		proc.setCaller_token(call.get("caller_token"));
		proc.setCaller(call.get("caller"));
		proc.setCaller_handle(call.get("caller_handle"));
		
		// resolve location		
		String slatitude = call.get("latitude");
		String slongitude = call.get("longitude");
		String location = call.get("location");
		String stz = call.get("timezone");
		String stime = call.get("time");
		double lon = 0, lat = 0;
		int tzoffset = -1;
		//System.out.println("CALL: " + call.toString());
		
		String key = SCServer.getChatServer().getSedro_access_key();
		if (slatitude == null || slongitude == null || stz == null) {	
			// api resolve[phone number/ipaddress]
			String phonenumber = call.get("phonenumber");
			String ip = call.get("ip_address");
			HashMap<String, Object> li = null;
			if (phonenumber != null) {
				li = RestExample.getPhoneInfoGET(key, phonenumber);			
			} else if (ip != null) {
				li = RestExample.getIPInfoGET(key, ip);			
			}
			if (li != null) {
				if (li.get("latitude") != null) lat = (Double)li.get("latitude");
				if (li.get("longitude") != null) lon = (Double)li.get("longitude");
				if (li.get("location") != null) location = (String)li.get("location");
				if (li.get("tzoffset") != null) tzoffset = (Integer)li.get("tzoffset");
				if (stz == null && li.get("tz") != null) stz = (String)li.get("tz");
				//System.out.println("GOT INFO: " + li.toString());
			}
		} else {
			lat = Sutil.toDouble(slatitude);
			lon = Sutil.toDouble(slongitude);
		}		
		proc.setLocation(lat, lon, location);
		if (debug_callinfo) System.out.println("NEW_CONN: lat: " + lat + " lon: " + lon + "  location: " + location);


		// resolve timezone / time
		if (tzoffset == -1 && stz == null) {
			// need to resolve from location
			HashMap<String, Object> li = RestExample.getLocationInfoGET(key, lat, lon);	
			if (li != null) {
				if (li.get("tzoffset") != null) tzoffset = (Integer)li.get("tzoffset");
				if (stz == null && li.get("tz") != null) stz = (String)li.get("tz");	
			}			
		}
		if (tzoffset == -1 && stz != null) {
			TimeZone tz = TimeZone.getTimeZone(stz);
			tzoffset = tz.getOffset(new Date().getTime()) / 1000 / 60;   //yields +120 minutes
		} 

		proc.setCalltime(stime, stz, tzoffset);
		if (debug_callinfo) System.out.println("NEW_CONN: tzoff: " + tzoffset + " tz: " + stz + "  time: " + stime);
			   
		// what to do with other info?
		proc.setCall_info(call);
		return proc;
	}
	
	//////////////////////////////////////////////////////
	// process the conversation
	public void process() {
		//System.out.println("  ORATOR _ PROC: " + getProcessorCount());
		if (!service.isPolled()) return;
		
		// this where we look for new calls 
		if (service.isPrivateMsg()) {
			List<HashMap<String, String>> newCalls = service.getDirectCall(this);
			if (newCalls != null && newCalls.size() > 0) {	
				for (HashMap<String, String> call:newCalls) addNewCall(call);
			}
		}
		
		if (getProcessorCount() > 0) {
			// if there are any conversations ... deal
			for (SCSedro s:processors) process(s);
		}	
		
		// clear the cache
		this.getChatService().clearCache();
		// save state (also re-saves config/everything)
		SCServer.getChatServer().save();
	}
	
	
	private void process(SCSedro processor) {
		int procCnt = 0;

		if (debug) System.out.println("\nPROCESS_["+processor.getStatus()+"]["+processor.getPersona()+"] => ["+processor.getCaller_handle()+"]");
		
		List<HashMap<String, Object>> wake_msg = null;
		String wmsg = null;
		
		
		//////////////////////////////////////////////////////
		// wake the persona
		if (processor.getStatus().equals("wake")) {
			wake_msg = processor.chatWake(server.getSedro_access_key(), wmsg);
			procCnt++;
			//System.out.println(" WAKE_WOKE["+processor.getStatus()+"]["+processor.getPersona()+"] msg: " + processor.getMsgNumber());
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
							String smsg = getFinalMessage(service.getName(), processor, true, m, (String)m.get("msg"));
							if (smsg == null) continue;
							procCnt++;
							service.postMessage(processor, smsg);
							if (debug) System.out.println("PUB_RESP: " + smsg);
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
				for (HashMap<String, Object> m:wake_msg) {
					String smsg = getFinalMessage(service.getName(), processor, false, m, (String)m.get("msg"));
					if (smsg == null) continue;
					if (debug) System.out.println("    outWMSG["+processor.getCaller_handle()+"]: " + smsg);
					String to = null;
					if (send_timed) {
						addMsg(smsg, (String)m.get("event"), m.get("pre_wait"), m.get("post_wait"), processor.getCaller_handle(), to, processor);
					} else {
						service.sendDirectMessage(processor, processor.getCaller_handle(), smsg);
					}
				}
			}

			// Deal with direct messages
			List<HashMap<String, String>> dml = service.getDirectMessages(this, processor);
			if (dml != null) {
				for (HashMap<String, String> mm:dml) {
					String msg = mm.get("msg");
					String from = mm.get("from");
					if (debug) System.out.println(" inMSG["+from+"]: " + msg);
					// private direct messages => private direct response
					procCnt++;
					List<HashMap<String, Object>> rmsg = processor.chatMsg(msg);
					if (rmsg != null) {
						for (HashMap<String, Object> m:rmsg) {
							String smsg = getFinalMessage(service.getName(), processor, false, m, (String)m.get("msg"));
							if (smsg == null) continue;
							String to = null;
							if (debug) System.out.println("    outMSG["+from+"]: " + smsg);
							// NOTE: from/to reverse
							if (send_timed) {
								addMsg(smsg, (String)m.get("event"), m.get("pre_wait"), m.get("post_wait"), from, to, processor);
							} else {
								// send direct message
								service.sendDirectMessage(processor, from, smsg);
							}
						}
					}
				}	
			}
		}
		
		
		//////////////////////////////////////////////////////	
		// poll for new messages to post/send
		if (procPoll && procCnt == 0) {
			List<HashMap<String, Object>> dml = processor.chatPoll();
			for (HashMap<String, Object> mm:dml) {
				String msg = (String)mm.get("msg");
				String from = (String)mm.get("from");
				if (debug) System.out.println(" inMSG["+from+"]: " + msg);
				// private direct messages => private direct response
				procCnt++;
				List<HashMap<String, Object>> rmsg = processor.chatMsg(msg);
				if (rmsg != null) {
					for (HashMap<String, Object> m:rmsg) {
						String smsg = getFinalMessage(service.getName(), processor, false, m, (String)m.get("msg"));
						if (smsg == null) continue;
						String to = null;
						if (debug) System.out.println("    outPMSG["+from+"]: " + smsg);
						// NOTE: from/to reverse
						if (send_timed) {
							addMsg(smsg, (String)m.get("event"), m.get("pre_wait"), m.get("post_wait"), from, to, processor);
						} else {
							// send direct message
							service.sendDirectMessage(processor, from, smsg);
						}
					}
				}
			}
		}
		
		if (debug) {
			System.out.println(" PROC["+processor.getStatus()+"]["+processor.getPersona()+"] msg: " + processor.getMsgNumber());
			System.out.println("");
		}
	}
	
	
	// Add message to the message QUEUE
	private void addMsg(String txt, String event, Object pre_wait, Object post_wait, String to, String from, SCSedro processor) {
		HashMap<String, Object> smsg = new HashMap<>();
		
		Integer wi = 0, pwi = 0;
		if (post_wait instanceof Integer) wi = (Integer)post_wait;
		else if (post_wait instanceof String) wi = Sutil.toInt((String)post_wait);		
		if (pre_wait instanceof Integer) wi = (Integer)pre_wait;
		else if (pre_wait instanceof String) wi = Sutil.toInt((String)pre_wait);			
		smsg.put("pre_wait", wi);
		smsg.put("post_wait", pwi);		
		
		smsg.put("msg", txt);	
		smsg.put("event", event);
		smsg.put("to", to);
		smsg.put("from", from);
		smsg.put("processor", processor);

		synchronized(msg_set) {
			HashMap<String, Object> lmsg = null;
			if (msg_set.size() > 0) lmsg = msg_set.get(msg_set.size()-1);	
			long stime = 0;
			if (lmsg != null) {
				stime = (Long)smsg.get("stime");
				Integer lw = (Integer)smsg.get("post_wait");
				if (lw != null) stime += lw;
				Integer pw = (Integer)smsg.get("pre_wait");
				if (pw != null) stime += pw;	
			} else {
				stime = Sutil.getUTCTime().getTimeInMillis();
				// t = now + pre_wait
				Integer pw = (Integer)smsg.get("pre_wait");
				if (pw != null) stime += pw;	
			}
			smsg.put("stime", stime);		
			msg_set.add(smsg);
		}
	}

	
	//////////////////////////////////////////////////////	
	// Process incoming
	public void processMessage(HashMap<String, String> call) {
		String hd = (String)call.get("caller_handle");
		String wmsg = (String)call.get("msg");
		SCSedro processor = this.findProcessor(hd);
		if (processor == null) {
			System.out.println("ORAT: new session: " + hd);
			processor = addNewCall(call);
		} 
		List<HashMap<String, Object>> wake_msg = null;
		if (!wake_text) wmsg = null;
			
		//////////////////////////////////////////////////////
		// wake the persona
		if (processor.getStatus().equals("wake")) {
			wake_msg = processor.chatWake(server.getSedro_access_key(), wmsg);
			//System.out.println(" WAKE_WOKE["+processor.getStatus()+"]["+processor.getPersona()+"] msg: " + processor.getMsgNumber());
		}
		
		//////////////////////////////////////////////////////
		// PRIVATE MESSAGES
		if (processor.isDirectMsg()) {
			if (wake_msg != null) {
				// where to send these messages?
				for (HashMap<String, Object> msg:wake_msg) {
					String smsg = getFinalMessage(service.getName(), processor, false, msg, (String)msg.get("msg"));
					if (smsg == null) continue;	
					System.out.println("    outWMSG["+processor.getCaller_handle()+"]: " + smsg);
					service.sendDirectMessage(processor, processor.getCaller_handle(), smsg);
				}
			}
			
			if (!wake_text) {
				// Deal with direct messages
				String msg = call.get("msg");
				String from = call.get("from");
				System.out.println(" inMSG["+from+"]: " + msg);
				// private direct messages => private direct response
				List<HashMap<String, Object>> rmsg = processor.chatMsg(msg);
				if (rmsg != null) {
					for (HashMap<String, Object> m:rmsg) {
						String smsg = getFinalMessage(service.getName(), processor, false, m, (String)m.get("msg"));
						if (smsg == null) continue;					
						// send direct message
						System.out.println("    outMSG["+from+"]: " + smsg);
						service.sendDirectMessage(processor, from, smsg);
					}
				}
			}

		}
	}

	
}
