package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class UsersHandler implements DCCommand {

	/**
	 * Gets the list of all NICKS registered on the current server identified by
	 * param: handler
	 * 
	 * @author madhavan
	 * 
	 * @param handler
	 * @return
	 * @throws InterruptedException
	 */

	public ArrayBlockingQueue<DCMessage> nick_q = new ArrayBlockingQueue<>(
			1024);// This Queue of all the nicks on the server.

	public void addNick(DCMessage nick) throws InterruptedException {
		nick_q.put(nick);
		// DCLogger.Log("Adding to list : "+nick.toString());
		return;
	}

	public void deleteNick(String nick) {
		Iterator<DCMessage> it = nick_q.iterator();
		while (it.hasNext()) {
			DCMessage nick_obj = it.next();
			if (nick_obj.myinfo.nick.equals(nick)) {
				nick_q.remove(nick_obj);
				// DCLogger.Log("Removing from list : "+nick);
			}
		}
		return;
	}

	/**
	 * 
	 * @param nick
	 *            - Nick to download from
	 * @param s
	 *            - Socket connection
	 * @param remote_filename
	 *            - Path to the file on remote user, from where to be downloaded
	 * @param local_filename
	 *            - Path where the downloaded file should be saved
	 * @return
	 */

	public boolean downloadFromUser(String nick, Socket s,
			String remote_filename, String local_filename) {
		

		return true;
	}
	
	public class downloadManager implements Runnable{

		public String nick;
		public String remote_filename;
		public String local_filename; 
		

		
		public downloadManager(String nick, String remote_filename,
				String local_filename) {
			super();
			this.nick = nick;
			this.remote_filename = remote_filename;
			this.local_filename = local_filename;
		}



		@Override
		public void run() {
			int available_ports = 15142;
			
			// Detect and bind to a port that is available
			ServerSocket socket_listener;
			while(true){
				try{
					socket_listener = new ServerSocket(available_ports);
				}
				catch(IOException e){
					available_ports++;
					continue;
				}
				break;
			}
			
			String my_ip = DCPreferences.get_self_ip();
			DCUser target_user = new DCUser();
			target_user.nick = nick;
			
			
			
			
			
		}
		
	}

	@Override
	public void onCommand(DCMessage msg) {
		String command = msg.command;
		if(command.equals("Quit")){
			String quit_command = msg.toString();
			String[] quit_arr = quit_command.split(":");
			try{
			String nick_to_delete = quit_arr[1].trim();
			deleteNick(nick_to_delete);
			}
			catch(ArrayIndexOutOfBoundsException e){
				DCLogger.Log("ERROR (003-001)");
			}
		}
		else if(command.equals("MyINFO")){
			try {
				addNick(msg);
			} catch (InterruptedException e) {
				DCLogger.Log("ERROR (003-002)");
			}			
		}
	}
	
	

}
