package com.phinvader.libjdcpp;

import java.util.concurrent.ArrayBlockingQueue;

public class UsersHandler {

	/**
	 * Gets the list of all NICKS registered on the current server identified by param: handler
	 * 
	 * @author madhavan
	 * 
	 * @param handler
	 * @return
	 * @throws InterruptedException
	 */

	public static ArrayBlockingQueue<String> getUsers(MessageHandler handler ) throws InterruptedException{
		ArrayBlockingQueue<String> nick_q = new ArrayBlockingQueue<>(1024);
		while (true) {
			DCMessage msg = handler.getNextMessage();
			if (msg.command != null && msg.command.equals("MyINFO")) {
				DCLogger.Log("USERSHANDLER LOG "+msg.myinfo.toString());
				nick_q.put(msg.myinfo.nick);
			} else if (msg.command != null && msg.command.equals("HubQuit"))
				break;
			else {
				//DCLogger.Log("USERSHANDLER LOG " + msg.toString());
			}
		}
		nick_q.put("COLLECTOR_QUIT");
		return nick_q;
		
	}
	
}
