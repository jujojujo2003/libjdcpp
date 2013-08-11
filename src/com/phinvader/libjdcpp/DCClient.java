package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

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
	private UsersHandler mainUserHandler;
	private MessageRouter mr;
	private DCDownloader downloadHandler ;
	
	public long getDownloadBytes(){
		return downloadHandler.getDownloadStatus();
	}
	

	
	public long getDownloadFileFullSize(){
		return downloadHandler.getDownloadFileFullSize();
	}

	
	
	
	public static class NotifyUsersChange extends UsersHandler{
		/* Just a stub */
	}
	
	public static class PassiveDownloadConnection extends DCRevconnect{

		public PassiveDownloadConnection(DCUser target_user, DCUser my_user,
				DCPreferences prefs, String local_filename,
				String remote_filaname) {
			super(target_user, my_user, prefs, local_filename, remote_filaname);
			// TODO Auto-generated constructor stub
		}
		/* Just a stub */
	}
	
	public static class BasicCallbackHandler implements DCCommand{

		@Override
		public void onCommand(DCMessage msg) {
			/* This is just a stub */
		}
		
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

		ArrayList<String> supported_methods = new ArrayList<String>();
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
	
	public void bootstrap(){
		mr = new MessageRouter(handler);
	}
	
	public void InitiateDefaultRouting(){
		
		mainUserHandler = new UsersHandler();
		mr.subscribe("MyINFO", mainUserHandler);
		mr.subscribe("Quit", mainUserHandler);
		Thread routing_thread = new Thread(mr);
		routing_thread.start();
	}
	
	public void setUserChangeHandler(DCCommand handler){
		mr.subscribe("MyINFO",handler);
		mr.subscribe("Quit", handler);
	}
	public void setCustomUserChangeHandler(DCCommand handler){
		mr.customSubscribe("MyINFO", handler);
		mr.customSubscribe("Quit", handler);
	}
	
	
	public void setPassiveDownloadHandler(DCUser t, DCUser m, DCCommand o){
		mr.subscribe("ConnectToMe", o);
		handler.send_revconnect(m,t);
	}
	
	public void setSearchHandler(DCCommand handler){
		mr.subscribe("SR", handler);
	}
	public void setCustomSearchHandler(DCCommand handler){
		mr.customSubscribe("SR", handler);
	}
	
	public void setCustomBoardMessageHandler(DCCommand handler){
		mr.subscribe("BoardMessage", handler);
	}
	
	public void setCustomRawHandler(String command, DCCommand handler){
		mr.subscribe(command, handler);
	}
	
	public void searchForFile(String key, DCUser myuser){
		handler.send_search(key, myuser);
	}
	
	
	public void startDownloadingFile(DCRevconnect dcRevconnect, DCUser myuser, String local_filename, String remote_filename){
		
		downloadHandler = new DCDownloader();
		
		DCDownloader.downloadManager dm = downloadHandler.new downloadManager(dcRevconnect, myuser, remote_filename, local_filename);
		Thread dm_thread = new Thread(dm);
		dm_thread.start();

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
		ArrayList<DCUser> nick_array = new ArrayList<DCUser>();
		Iterator<DCUser> it = mainUserHandler.nick_q.iterator();
		while(it.hasNext()){
			nick_array.add(it.next());
		}
		return nick_array;
	}
}
