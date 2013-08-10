package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.phinvader.libjdcpp.BasicClientv1.MyDCRevconnect;

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
	
	
	private Socket DCConnectionSocket ;
	private MessageHandler handler;
	private UsersHandler uh;
	private MessageRouter mr;

	
	
	
	public static class NotifyUsersChange extends UsersHandler{
		/* Just a stub */
	}
	
	public static class PassiveDownloadConnection extends DCRevconnect{
		/* Just a stub */
	}
	
	
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
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws InterruptedException 
	 * 
	 * @see DCClient#connect(String, DCPreferences)
	 * 
	 */
	public void connect(String ip, int port, DCPreferences pref) throws UnknownHostException, IOException, InterruptedException {

		DCConnectionSocket = new Socket(ip, port);
		handler = new MessageHandler(DCConnectionSocket);
		DCMessage lock = handler.getNextMessage();
		DCMessage hubname = handler.getNextMessage();
		DCLogger.Log("Connected to :" + hubname);

		ArrayList<String> supported_methods = new ArrayList<>();
		supported_methods.add("NoHello");
		supported_methods.add("NoGetINFO");

		handler.send_supports(supported_methods);
		handler.send_key(DCFunctions.convert_lock_to_key(lock.lock_s
				.getBytes()));
		
		handler.send_validatenick(pref.getNick());

		while (true) {
			DCMessage msg = handler.getNextMessage();
			DCLogger.Log(msg.toString());
			if (msg.command != null)
				if (msg.command.equals("Hello")
						&& msg.hello_s.equals(pref.getNick()))
					break;
		}
		
		handler.send_version();
		handler.send_getnicklist();
		
		DCUser myuser = new DCUser();
		myuser.nick = pref.getNick();
		
		handler.send_myinfo(myuser);
	}
	
	public void InitiateDefaultRouting(){
		mr = new MessageRouter(handler);
		
		uh = new UsersHandler();
		mr.subscribe("MyINFO", uh);
		mr.subscribe("Quit", uh);
		Thread routing_thread = new Thread(mr);
		routing_thread.start();
	}
	
	public void setUserChangeHandler(DCCommand o){
		mr.subscribe("MyINFO",o);
		mr.subscribe("Quit", o);
	}
	
	public void setPassiveDownloadHandler(DCUser t, DCUser m, DCCommand o){
		mr.subscribe("ConnectToMe", o);
		handler.send_revconnect(m,t);
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
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws InterruptedException 
	 * 
	 * @see DCClient#connect(String, int, DCPreferences)
	 */
	public void connect(String ip, DCPreferences pref) throws UnknownHostException, IOException, InterruptedException {
		connect(ip, 411, pref);
	}
	
	/**
	 * @return The list of users registered with this hub.
	 */
	public ArrayList<DCUser> get_nick_list() { 
		
		return null;
	}
}
