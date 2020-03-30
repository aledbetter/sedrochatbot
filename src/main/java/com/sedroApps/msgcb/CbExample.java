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
package main.java.com.sedroApps.msgcb;

import java.util.HashMap;

import main.java.com.sedroApps.SCCall;


public class CbExample extends CbMessage {

	public CbExample(String name) {
		super(name);
	}
	
	@Override
	public String getFinalMessage(String caname, SCCall processor, boolean msgPublic, 
			HashMap<String, Object> msgInfo, String msg) {
		// just a simple replace
		msg.replaceAll("yes", "maybe");
		
		return null;
	}

}
