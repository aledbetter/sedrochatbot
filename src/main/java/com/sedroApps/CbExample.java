package main.java.com.sedroApps;

import java.util.HashMap;


public class CbExample extends CbMessage {

	public CbExample(String name) {
		super(name);
	}
	
	@Override
	public String getFinalMessage(String caname, Sedro processor, boolean msgPublic, 
			HashMap<String, Object> msgInfo, String msg) {
		// just a simple replace
		msg.replaceAll("yes", "maybe");
		
		return null;
	}

}
