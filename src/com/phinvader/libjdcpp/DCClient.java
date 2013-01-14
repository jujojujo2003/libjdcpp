package com.phinvader.libjdcpp;

import java.net.InetAddress;
import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

/**
 * 
 * This is the core main class which serves as the DC Client , all operations
 * occur through interfacing with a connected instance of this object. The
 * DCClient must be connected before any operations can take place
 * 
 * @author phinfinity
 * @see DCPreferences
 */
public class DCClient {
	private String HubName;
	InetAddress HubAddress;
	int HubPort;

	/**
	 * 
	 * This function is used to connect to a DC-enabled hub on the specified ip
	 * address and port. The DCClient object must first be connected before any
	 * other operations can take place
	 * 
	 * @param ip
	 *            - IP Address of Server to Connect to
	 * @param port
	 *            - Port Number to connect to (default 411)
	 * 
	 * @param pref
	 *            - Preference object containing user prefences , such as
	 *            nickname
	 * 
	 * @see DCClient#connect(String, DCPreferences)
	 * 
	 */
	public void connect(String ip, int port, DCPreferences pref) {
		// TODO fill code here
	}

	/**
	 * Connects using the default port 411.
	 * 
	 * @param ip
	 *            - IP Address of Server to Connect to
	 * 
	 * @param pref
	 *            - Preference object containing user prefences , such as
	 *            nickname
	 * 
	 * @see DCClient#connect(String, int, DCPreferences)
	 */
	public void connect(String ip, DCPreferences pref) {
		connect(ip, 411, pref);
	}
	
	/**
	 * @return The list of users registered with this hub.
	 */
	public ArrayList<DCUser> get_nick_list() { 
		return null;
	}
}
