package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import com.phinvader.libjdcpp.DCRevconnect.DownloadStatus;

public class UsersHandler implements DCCommand {

	/**
	 * Gets the list of all NICKS registered on the current server identified by
	 * param: handler
	 * 
	 * To use UserHandler: class MyUserHandler extends UserHandler implements
	 * DCCallback
	 * 
	 * 
	 * @author madhavan
	 * 
	 * @param handler
	 * @return
	 * @throws InterruptedException
	 */

	public ArrayBlockingQueue<DCUser> nick_q = new ArrayBlockingQueue<>(1024);
	
	/**
	 * Add a user to the list-of-online-users Updated when there is a MYINFO
	 * Message from the server
	 * 
	 * @param nick
	 * @throws InterruptedException
	 */

	public void addNick(DCMessage nick) throws InterruptedException {
		nick_q.put(nick.myinfo);
		return;
	}

	/**
	 * Delete a user from the list-of-online-users This is updated when there is
	 * a QUIT message from the server
	 * 
	 * @param nick
	 */

	public void deleteNick(String nick) {
		Iterator<DCUser> it = nick_q.iterator();
		while (it.hasNext()) {
			DCUser nick_obj = it.next();
			if (nick_obj.nick.equals(nick)) {
				nick_q.remove(nick_obj);
				// DCLogger.Log("Removing from list : "+nick);
			}
		}
		return;
	}
	
	/**
	 * DCCommand interface override
	 * Should subscribe to MyInfo and Quit to the router.
	 * 
	 * @param msg
	 */

	@Override
	public void onCommand(DCMessage msg) {
		String command = msg.command;
		if (command.equals("Quit")) {
			String quit_command = msg.toString();
			String[] quit_arr = quit_command.split(":");
			try {
				String nick_to_delete = quit_arr[1].trim();
				deleteNick(nick_to_delete);
			} catch (ArrayIndexOutOfBoundsException e) {
				DCLogger.Log("ERROR (003-001)");
			}
		} else if (command.equals("MyINFO")) {
			try {
				addNick(msg);
			} catch (InterruptedException e) {
				DCLogger.Log("ERROR (003-002)");
			}
		}
	}

}
