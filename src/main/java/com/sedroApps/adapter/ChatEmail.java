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

package main.java.com.sedroApps.adapter;


import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import main.java.com.sedroApps.SCOrator;
import main.java.com.sedroApps.SCSedroCall;
import main.java.com.sedroApps.SCUser;
import main.java.com.sedroApps.util.Sutil;

// FIXME imap only for now


public class ChatEmail extends ChatAdapter { 

	private Session mSession;
	private Store mStore;
	private Folder mFolder;
	
	// this is per user?
	private String pusername = null;  	// username
	private String ppassword = null; 	// password
	private String pprotocol = "imaps";
	private String pfolder = "INBOX";
	private String phost = "imap.gmail.com";
	private int pport = 993;
	private String pemail = null; 		// email address
	private String pfrom_email = null; 	// email address
	private String preplyto_email = null; 	// email address

	
	public ChatEmail(SCUser user, String id) {
		super(user, id);
	}
	
	@Override
	public String getName() {
		return "email";	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// EXTERNAL calls: init & processing
	@Override
	public int init(SCUser ua) {
		super.init(ua);
		String protocol = getServiceInfo("protocol");
		String host = getServiceInfo("host");
		int port = Sutil.toInt(getServiceInfo("port"));
		String folder = getServiceInfo("folder");
		String username = getServiceInfo("username");
		String password = getServiceInfo("password");
		String email = getServiceInfo("email");
		String from_email = getServiceInfo("from_email");
		String replyto_email = getServiceInfo("replyto_email");
		if (username == null || password == null || host == null || protocol == null || email == null) {
			return -1; // not configured... remove
		}
		
		if (isLoggedIn()) {
			// check for updates..
			boolean change = false;
			if (!pusername.equals(username)) change = true;
			if (!ppassword.equals(password)) change = true;
			if (!Sutil.compare(pprotocol, protocol)) change = true;
			if (!Sutil.compare(phost, host)) change = true;
			if (port != pport) change = true;
			if (!Sutil.compare(pfolder, folder)) change = true;
			if (!Sutil.compare(pemail, email)) change = true;
			if (!Sutil.compare(pfrom_email, from_email)) change = true;
			if (!Sutil.compare(preplyto_email, replyto_email)) change = true;
			if (!change) return 0;
			// logout
			this.logout();
		}

    	// retain config
    	pusername = username;
    	ppassword = password;
    	pprotocol = protocol;
    	phost = host;
    	pport = port;
    	pfolder = folder;
    	pemail = email;
    	pfrom_email = from_email;
    	preplyto_email = replyto_email;
    	return 0;
	}
	
	
	@Override
	public String sendDirectMessage(SCSedroCall proc, String touser, String msg) {
      try{  
          MimeMessage message = new MimeMessage(mSession);  
          String from = this.pfrom_email;
          if (from == null) from = this.pemail;
          
          message.setFrom(new InternetAddress(from));   
       
          if (this.preplyto_email != null) {
        	  Address address = new InternetAddress(this.preplyto_email);
        	  Address adr [] = {address};
        	  message.setReplyTo(adr);
          }
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(touser));  
          message.setText(msg);  

         String subject = "FIXME";
          message.setSubject(subject);  

          // Send message  
          Transport.send(message);  
          System.out.println("message sent successfully....");  
          

          /*
          // Send the message by authenticating the SMTP server
          // Create a Transport instance and call the sendMessage
          Transport t = session.getTransport("smtp");
          try {
     //connect to the smpt server using transport instance
     //change the user and password accordingly	
         t.connect("abc", "****");
         t.sendMessage(replyMessage, replyMessage.getAllRecipients());
          } finally {
             t.close();
          }
          System.out.println("message replied successfully ....");
   
          */
          
          
       } catch (MessagingException mex) {
    	   mex.printStackTrace();
       }
		return "ERROR";			
	}

	//////////////////////////////////////////////////
	// Polling handlers	
	// list of messages: from:from user / msg:message text
	//https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json
	@Override	
	public List<HashMap<String, String>> getDirectCall(SCOrator orat) {	
		List<HashMap<String, String>> cl = null;

		/*
		List<HashMap<String, String>> ml = getMessages(orat);
		if (ml == null || ml.size() < 1) return null;
		for (HashMap<String, String> msg:ml) {
			// find session...					
			String from = msg.get("from");
			SCSedro proc = orat.findProcessor(from);					
			if (proc != null) continue; // check for new calls only	
			if (cl != null) {
				// check if accounted for
				boolean dup = false;
				for (HashMap<String, String> m:cl) {
					String tf = m.get("from");
					if (tf.equals(from)) {
						dup = true;
						break;
					}
				}
				if (dup) continue;
			}
			if (cl == null) cl = new ArrayList<>();
			cl.add(msg);
			//System.out.println("   NEW["+cl.size()+"]: " +from);			
		}
		*/
		return cl;
	}
	
	// list of messages: from:from user / msg:message text
	@Override	
	public List<HashMap<String, String>> getDirectMessages(SCOrator orat, SCSedroCall processor) {
		if (!this.login()) {
			return null;
		}
		// get messages
		Message [] msgs = getMessages();
		if (msgs == null || msgs.length < 1) return null;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datefmt);

		List<HashMap<String, String>> dl = new ArrayList<>();
		for (int i = 0; i < msgs.length; i++) {
			HashMap<String, String> m = new HashMap<>();
			try {
				if (msgs[i].getSubject() != null) m.put("subject", msgs[i].getSubject());
				Address[] recpients = msgs[i].getAllRecipients();
				// FIXME limit to single eamil address?				
				System.out.println("EMAIL_IN["+recpients.length+"]");
		//		if (pphone_number != null) {
		//		}
//FIXME
//				mm.put("to", record.getTo());

				Address[] fromAddress = msgs[i].getFrom();
				String from = fromAddress[0].toString();
				if (!from.equals(processor.getCaller_handle())) continue;	
				m.put("caller_handle", from);
				m.put("caller", from);
				m.put("email", from);
				
				Date sent = msgs[i].getSentDate();
				m.put("date_created", simpleDateFormat.format(sent));
				
				try {
					String content = msgs[i].getContent().toString();
					m.put("msg", content);			 
				} catch (IOException e) {}

				m.put("status", "recieved");
				String hdrs[] = msgs[i].getHeader("message-id");
				if (hdrs != null && hdrs.length > 0) m.put("id", hdrs[0]);

			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}		
		return dl;
	}
	
	@Override	
	public void clearCache() {
		//msg_set = null;
	}

	
	private boolean isLoggedIn() {
		return mStore.isConnected();
	}

	// login to the mail host server
	public boolean login() {
		URLName url = new URLName(pprotocol, phost, pport, pfolder, pusername, ppassword);
		if (mSession == null) {
			Properties props = null;
			try {
				props = System.getProperties();
			} catch (SecurityException sex) {
				props = new Properties();
			}
			mSession = Session.getInstance(props, null);
		}
		try {
			mStore = mSession.getStore(url);
			mStore.connect();
			mFolder = mStore.getFolder(url);
			if (mFolder == null) return false;
		
			mFolder.open(Folder.READ_WRITE);
			return true;
		
		} catch (Throwable t) {}
		return false;
	}

	// logout from the mail host server
	private void logout() {
		try {
			mFolder.close(false);
			mStore.close();
		} catch (Throwable t) {}
		mStore = null;
		mSession = null;
	}

	private Message[] getMessages() {
		try {
		return mFolder.getMessages();
		} catch (Throwable t) {}
		return null;
	}

	private void sendMessage(String from, String to, String subject, String text) {
	      try{  
	          MimeMessage message = new MimeMessage(mSession);  
	          message.setFrom(new InternetAddress(from));  
	          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));  
	          message.setSubject(subject);  
	          message.setText(text);  
	//          message.setReplyTo(original.getReplyTo());

	          // Send message  
	          Transport.send(message);  
	          System.out.println("message sent successfully....");  
	          

	          /*
              // Send the message by authenticating the SMTP server
              // Create a Transport instance and call the sendMessage
              Transport t = session.getTransport("smtp");
              try {
   	     //connect to the smpt server using transport instance
	     //change the user and password accordingly	
             t.connect("abc", "****");
             t.sendMessage(replyMessage, replyMessage.getAllRecipients());
              } finally {
                 t.close();
              }
              System.out.println("message replied successfully ....");
	   
	          */
	          
	          
	       } catch (MessagingException mex) {
	    	   mex.printStackTrace();
	       }  
	}
	

}
