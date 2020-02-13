package main.java.com.sedroApps;

import java.util.List;


public class ChatService {

	public String getName() {
		return "none";	
	}
	
	public int init(UserAccount ua) {
		// over-ride for each service to set info needed
		return 0;
	}
	
	public int disconnnect(UserAccount ua) {
		// over-ride for each service to set info needed
		return 0;
	}
	
	public String postMessage(String msg) {
		return "NOP";	
	}

	public String sendDirectMessage(String touser, String msg) {
		return "NOP";			
	}
	
	public List<String> getTimeLine() {
		return null;
	}
}
