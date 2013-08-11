package com.phinvader.libjdcpp;

import java.util.HashMap;

/**
 * This is the thread that will perpetually listen for MESSAGES (msg) from the
 * DC server and route the message to an appropriate handler using the
 * subscriptions model.
 * 
 * @author madhavan
 * 
 */

public class MessageRouter implements Runnable {

	MessageHandler handler;
	HashMap<String, Object> subscriptions = new HashMap<String, Object>();
	DCPreferences prefs = null;

	
	/**
	 * An object is said to subscribe to a COMMAND, when it handles all the
	 * messages that are recieved with command flag=COMMAND
	 * 
	 * This object should implement interface DCCommand
	 * 
	 * @param command
	 *            - msg.COMMAND
	 * @param handler
	 *            - OBJECT
	 * @return
	 */
	public boolean subscribe(String command, DCCommand handler) {
		subscriptions.put(command, handler);
		return true;
	}

	public MessageRouter(MessageHandler h) {
		handler = h;
	}

	public void run() {

		while (true) {
			DCMessage msg = null;
			try {
				msg = handler.getNextMessage();
			} catch (InterruptedException e) {
				continue;
			}

			Object o = subscriptions.get(msg.command);
			DCCommand handle = (DCCommand) o;
			if (handle != null) {
				handle.onCommand(msg);
				try{
					// MyUserHandler should implement a onCallback()
					// To update UI and notify change in nick_q
					DCCallback callback_handle = (DCCallback) o;
					callback_handle.onCallback(msg,handler);
				}
				catch(Exception e){
					DCLogger.Log("ERROR (001-001)");
				}
				
				
			}
			else{
			// If message is not subscribed by any handler, discard.
				//if(msg.command!=null)
				//DCLogger.Log(msg.toString());
				
				continue;
			}

		}

	}

}
