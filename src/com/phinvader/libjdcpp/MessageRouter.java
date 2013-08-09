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

			DCCommand handle = (DCCommand) subscriptions.get(msg.command);
			if (handle != null) {
				handle.onCommand(msg);
			}
			else{
			// If message is not subscribed by any handler, discard.
				continue;
			}

		}

	}

}
