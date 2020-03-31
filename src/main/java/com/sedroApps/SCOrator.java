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
	
	private List<SCSedroCall> calls = null;
	
	private ChatAdapter service = null;
	private SCTenant server = null;
	private SCUser user = null;
	
	// messages to send to users
	private List<HashMap<String, Object>> msg_set = null;
	private Timer msg_timer = null;

	private SCCall callPublic = null;
	boolean procPoll = false;
	
	SCOrator(SCTenant server, ChatAdapter service, SCUser user, boolean readPublic, boolean respPublic) {
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
			callPublic = addSedroCall(readPublic, respPublic, false);
			callPublic.setChannel_type(service.getChannel_type());
			callPublic.setLanguage(service.getLanguage());
			callPublic.setContext(service.getContext());
			callPublic.setPersona(user.getSedroPersona());
		}
		service.setOrator(this);
		
		msg_set = new ArrayList<>();
		if (send_timed) {
			startSendTimer();
		}
	}
	
	public int getCallCount() {
		if (calls == null) return 0;
		return calls.size();
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
	            	SCSedroCall call = (SCSedroCall)m.get("call");
	               // System.out.println("TIMER Send("+sendList.size()+")["+service.getName()+"]["+from+"] => ["+to+"]: " + txt);

					service.sendDirectMessage(call, to, txt);
    			}
            }
        }, 100, 100);		
	}
	
	// close 
	public void close() {
		if (getCallCount() > 0) {
			for (SCSedroCall s:calls) {
				// close all calls
				List<HashMap<String, Object>> msg = s.chatByeMessages();
			}
		}
		if (msg_timer != null) msg_timer.cancel();
	}
	private SCCall addSedroCall(boolean readPublic, boolean respPublic, boolean directMsg) {
		SCSedroCall s = new SCSedroCall(getChatService(), readPublic, respPublic, directMsg);
		if (calls == null) calls = new ArrayList<>();
		calls.add(s);
		return s;
	}
	public void removeCall(SCSedroCall s) {
		if (calls == null) return;
		calls.remove(s);
	}
	
	public SCCall findCall(String caller_handle) {
		if (getCallCount() > 0) {
			for (SCSedroCall s:calls) {
				// close all calls
				if (Sutil.compare(s.getCaller_handle(), caller_handle)) return s;
			}
		}
		return null;
	}
	public SCCall findCallByID(String id) {
		if (getCallCount() > 0) {
			for (SCSedroCall s:calls) {
				// close all calls
				if (Sutil.compare(s.getId(), id)) return s;
			}
		}
		return null;
	}
	
	//////////////////////////////////////////////////////	
	// check callbacks for message override
	private String getFinalMessage(String caname, SCCall call, boolean msgPublic, 
			HashMap<String, Object> msgInfo, String msg) {
		if (msg == null || msg.equals("null") || msg.isEmpty()) return null;
		CbMessage cb = this.user.getMessageCb();
		if (cb != null) return cb.getFinalMessage(caname, call, msgPublic, msgInfo, msg);
		return msg;
	}
	
	//////////////////////////////////////////////////////
	// Add processor for new call
	private SCCall addNewCall(HashMap<String, String> call) {
		// ADD NEW channels...calls
		SCCall proc = addSedroCall(false, false, true);
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
		
		String key = SCTenant.getChatServer().getSedro_access_key();
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
			HashMap<String, String> li = RestExample.getLocationInfoGET(key, lat, lon);	
			if (li != null) {
				Object o = li.get("tzoffset");
				if (o != null) {
					if (o instanceof Integer) tzoffset = (Integer)o;
					else if (o instanceof String) tzoffset = Sutil.toInt((String)o);
				}
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
		
		if (getCallCount() > 0) {
			// if there are any conversations ... deal
			for (SCSedroCall s:calls) process(s);
		}	
		
		// clear the cache
		this.getChatService().clearCache();
		// save state (also re-saves config/everything)
		SCTenant.getChatServer().save();
	}
	
	
	private void process(SCSedroCall call) {
		int procCnt = 0;
		if (debug) System.out.println("\nPROCESS_["+call.getStatus()+"]["+call.getPersona()+"] => ["+call.getCaller_handle()+"]");
							
		if (call.isReadPublic()) {
			//////////////////////////////////////////////////////
			// PUBLIC MESSAGES
			
			///////////////////////////////////////
			// wake the persona
			if (call.isStatus("wake")) {
				List<HashMap<String, Object>> wake_msg = call.chatWakeMessages(server.getSedro_access_key(), null);
				procCnt++;
				//System.out.println(" WAKE_WOKE["+call.getStatus()+"]["+call.getPersona()+"] msg: " + call.getMsgNumber());
			}
			
			List<String> tml = service.getPublicMessages();
			if (tml != null) {
				for (String msg:tml) {
					System.out.println("PUB: " + msg);
					if (!call.isRespPublic()) continue;
					List<HashMap<String, Object>> rmsg = call.chatMsgMessages(msg);
					if (rmsg != null) {
						for (HashMap<String, Object> m:rmsg) {
							// send direct message
							String smsg = getFinalMessage(service.getName(), call, true, m, (String)m.get("msg"));
							if (smsg == null) continue;
							procCnt++;
							service.postMessage(call, smsg);
							if (debug) System.out.println("PUB_RESP: " + smsg);
						}
					}
				}
			}
		} else if (call.isDirectMsg()) {
			//////////////////////////////////////////////////////
			// PRIVATE MESSAGES

			// get any incomming messages for this caller
			List<HashMap<String, String>> dml = service.getDirectMessages(this, call);
	
			///////////////////////////////////////
			// wake the persona
			if (call.isStatus("wake")) {
				String wmsg = null, wfrom = null;
				if (dml != null) {
					// remove first message and send it with wake
					wmsg = dml.get(0).get("msg");
					wfrom = dml.get(0).get("from");
					dml.remove(0);	
					if (dml.size() < 1) dml = null;
					if (debug) System.out.println(" wakeMSG["+wfrom+"]: " + wmsg);
				}

				List<HashMap<String, Object>> wake_msg = call.chatWakeMessages(server.getSedro_access_key(), wmsg);
				procCnt++;
				//System.out.println(" WAKE_WOKE["+call.getStatus()+"]["+call.getPersona()+"] msg: " + call.getMsgNumber());
				if (wake_msg != null) {
					// where to send these messages?
					for (HashMap<String, Object> m:wake_msg) {
						String smsg = getFinalMessage(service.getName(), call, false, m, (String)m.get("msg"));
						if (smsg == null) continue;
						if (debug) System.out.println("    outWMSG["+call.getCaller_handle()+"]: " + smsg);
						String to = null;
						if (send_timed) {
							addMsg(smsg, (String)m.get("event"), m.get("pre_wait"), m.get("post_wait"), call.getCaller_handle(), to, call);
						} else {
							service.sendDirectMessage(call, call.getCaller_handle(), smsg);
						}
					}
				}
			}

			// Deal with direct messages
			if (dml != null) {
				for (HashMap<String, String> mm:dml) {
					String msg = mm.get("msg");
					String from = mm.get("from");
					if (debug) System.out.println(" inMSG["+from+"]: " + msg);
					// private direct messages => private direct response
					procCnt++;
					List<HashMap<String, Object>> rmsg = call.chatMsgMessages(msg);
					if (rmsg != null) {
						for (HashMap<String, Object> m:rmsg) {
							String smsg = getFinalMessage(service.getName(), call, false, m, (String)m.get("msg"));
							if (smsg == null) continue;
							String to = null;
							if (debug) System.out.println("    outMSG["+from+"]: " + smsg);
							// NOTE: from/to reverse
							if (send_timed) {
								addMsg(smsg, (String)m.get("event"), m.get("pre_wait"), m.get("post_wait"), from, to, call);
							} else {
								// send direct message
								service.sendDirectMessage(call, from, smsg);
							}
						}
					}
				}	
			}
		}
		
		
		//////////////////////////////////////////////////////	
		// poll for new messages to post/send
		if (procPoll && procCnt == 0) {
			List<HashMap<String, Object>> dml = call.chatPollMessages();
			for (HashMap<String, Object> mm:dml) {
				String msg = (String)mm.get("msg");
				String from = (String)mm.get("from");
				if (debug) System.out.println(" inMSG["+from+"]: " + msg);
				// private direct messages => private direct response
				procCnt++;
				List<HashMap<String, Object>> rmsg = call.chatMsgMessages(msg);
				if (rmsg != null) {
					for (HashMap<String, Object> m:rmsg) {
						String smsg = getFinalMessage(service.getName(), call, false, m, (String)m.get("msg"));
						if (smsg == null) continue;
						String to = null;
						if (debug) System.out.println("    outPMSG["+from+"]: " + smsg);
						// NOTE: from/to reverse
						if (send_timed) {
							addMsg(smsg, (String)m.get("event"), m.get("pre_wait"), m.get("post_wait"), from, to, call);
						} else {
							// send direct message
							service.sendDirectMessage(call, from, smsg);
						}
					}
				}
			}
		}
		
		if (debug) {
			System.out.println(" PROC["+call.getStatus()+"]["+call.getPersona()+"] msg: " + call.getMsgNumber());
			System.out.println("");
		}
	}
	
	
	// Add message to the message QUEUE
	private void addMsg(String txt, String event, Object pre_wait, Object post_wait, String to, String from, SCSedroCall call) {
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
		smsg.put("processor", call);

		synchronized(msg_set) {
			HashMap<String, Object> lmsg = null;
			if (msg_set.size() > 0) lmsg = msg_set.get(msg_set.size()-1);	
			long stime = 0;
			if (lmsg != null && smsg.get("stime") != null) {
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
	// Process incoming direct inline
	public HashMap<String, Object> processMessageFull(HashMap<String, String> call_info) {
		// find existing call
		String callid = (String)call_info.get("call_id");		
		SCCall call = null;	
		if (callid != null) {
			call = this.findCallByID(callid);
			if (call == null) {
				System.out.println("CALL NOT FOUND[" + callid  + "]  => " + call_info.toString());
				return null;
			}
		} 
		if (call == null) {
			call = addNewCall(call_info);
			//System.out.println(" NEW CALL: " + call.getStatus() + "  => " + callid);
		} 
		
		String wmsg = (String)call_info.get("msg");
		String event = call_info.get("event");
		//System.out.println("MSG[" + event+"] " + wmsg);
		HashMap<String, Object> r = null;
		//////////////////////////////////////////////////////
		// wake the persona
		if (Sutil.compare(event, "wake")) {
			r = call.chatWake(server.getSedro_access_key(), wmsg);
		} else if (Sutil.compare(event, "bye")) {
			r = call.chatBye();
		} else if (Sutil.compare(event, "poll")) {
			r = call.chatPoll();
		} else {
			r = call.chatMsg(wmsg);			
		}
		if (r != null) r.put("call_id", call.getId());
		return r;
	}
	
	//////////////////////////////////////////////////////	
	// Process incoming
	public String processMessage(HashMap<String, String> call_info) {		
		// find existing call
		String hd = (String)call_info.get("caller_handle");
		String callid = (String)call_info.get("call_id");
		SCCall call = null;	
		if (callid != null) {
			call = this.findCallByID(callid);
		}
		if (call == null && hd != null) {
			call = this.findCall(hd);
		}
		if (call == null) {
			System.out.println("ORAT: new call: " + hd+"/"+callid);
			call = addNewCall(call_info);
		}
		
		String wmsg = (String)call_info.get("msg");
		String event = call_info.get("event");
		
		boolean wake = false;
		if (Sutil.compare(event, "wake")) wake = true;

		List<HashMap<String, Object>> wake_msg = null;
			
		//////////////////////////////////////////////////////
		// wake the persona
		if (call.isStatus("wake")) {
			wake_msg = call.chatWakeMessages(server.getSedro_access_key(), wmsg);
			//System.out.println(" WAKE_WOKE["+call.getStatus()+"]["+call.getPersona()+"] msg: " + call.getMsgNumber());
		}
		
		//////////////////////////////////////////////////////
		// PRIVATE MESSAGES
		if (call.isDirectMsg()) {
			if (wake_msg != null) {
				// where to send these messages?
				for (HashMap<String, Object> msg:wake_msg) {
					String smsg = getFinalMessage(service.getName(), call, false, msg, (String)msg.get("msg"));
					if (smsg == null) continue;	
					System.out.println("    outWMSG["+call.getCaller_handle()+"]: " + smsg);
					service.sendDirectMessage(call, call.getCaller_handle(), smsg);
				}
			}
			
			if (!wake) {
				// Deal with direct messages
				String msg = call_info.get("msg");
				String from = call_info.get("from");
				System.out.println(" inMSG["+from+"]: " + msg);
				// private direct messages => private direct response
				List<HashMap<String, Object>> rmsg = call.chatMsgMessages(msg);
				if (rmsg != null) {
					for (HashMap<String, Object> m:rmsg) {
						String smsg = getFinalMessage(service.getName(), call, false, m, (String)m.get("msg"));
						if (smsg == null) continue;					
						// send direct message
						System.out.println("    outMSG["+from+"]: " + smsg);
						service.sendDirectMessage(call, from, smsg);
					}
				}				
			}
		}
		return null;
	}

	
}
