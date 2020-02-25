package main.java.com.sedroApps;

import java.util.HashMap;

abstract public class CbMessage {
	private String name;
	
	public CbMessage(String name) {
		this.setName(name);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/////////////////////////////////////////////////////////
	// implement and register this to make an override
	public abstract String getFinalMessage(String caname, Sedro processor, boolean msgPublic, 
											HashMap<String, Object> msgInfo, String msg);
}
