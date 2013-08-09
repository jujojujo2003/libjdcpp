package com.phinvader.libjdcpp;

import java.util.Iterator;
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

	
	public static ArrayBlockingQueue<DCMessage> nick_q = new ArrayBlockingQueue<>(1024);// This Queue of all the nicks on the server.
	
	public static void addNick(DCMessage nick) throws InterruptedException{
		nick_q.put(nick);
		//DCLogger.Log("Adding to list : "+nick.toString());
		return;
	}
	public static void deleteNick(String nick){
		Iterator<DCMessage> it = nick_q.iterator();
		while(it.hasNext()){
			DCMessage nick_obj = it.next();
			if(nick_obj.myinfo.nick.equals(nick)){
				nick_q.remove(nick_obj);
				//DCLogger.Log("Removing from list : "+nick);
			}
		}
		return;
	}
	
	

	
	/*
	public static ArrayBlockingQueue<String> startGetUsers(MessageHandler handler ) throws InterruptedException{
		ArrayBlockingQueue<String> nick_q = new ArrayBlockingQueue<>(1024);
		while (true) {
			DCMessage msg = handler.getNextMessage();
			if (msg.command != null && msg.command.equals("MyINFO")) {
				nick_q.put(msg.myinfo.nick);
			} else if (msg.command != null && msg.command.equals("HubQuit"))
				break;
			else {
				String[] quit_nick = msg.toString().split(":");
				if(quit_nick[0].trim().equals("Quit")){
					nick_q.remove(quit_nick[1].trim());
				}
			}
		}
		nick_q.put("COLLECTOR_QUIT");
		return nick_q;
		
	}
	*/
	
}
