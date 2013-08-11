package com.phinvader.libjdcpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
	HashMap<String, List<DCCommand>> subscriptions = new HashMap<String, List<DCCommand>>();
	HashMap<String, List<DCCommand>> customSubscriptions = new HashMap<String, List<DCCommand>>();
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
		List<DCCommand> freshList = new ArrayList<>();
		if (subscriptions.get(command) == null)
			subscriptions.put(command,freshList);
		List<DCCommand> handlersList = subscriptions.get(command);
		handlersList.add(handler);
		DCLogger.Log(handlersList.get(0).toString());
		subscriptions.put(command, handlersList);
		return true;
	}
	
	public boolean customSubscribe(String command, DCCommand handler) {
		List<DCCommand> freshList = new ArrayList<>();
		if (customSubscriptions.get(command) == null)
			customSubscriptions.put(command,freshList);
		List<DCCommand> handlersList = customSubscriptions.get(command);
		handlersList.add(handler);
		DCLogger.Log(handlersList.get(0).toString());
		customSubscriptions.put(command, handlersList);
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

			List<DCCommand> listOfSubscriptions = subscriptions
					.get(msg.command);
			if (listOfSubscriptions == null) {
				continue;
			} else {
				for (DCCommand dcCommand : listOfSubscriptions) {
					DCCommand handle = dcCommand;
					if (handle != null) {
						handle.onCommand(msg);
					}

				}
			}
			
			List<DCCommand> listOfCustomSubscriptions = customSubscriptions
					.get(msg.command);
			if (listOfCustomSubscriptions == null) {
				continue;
			} else {
				for (DCCommand dcCallback: listOfCustomSubscriptions) {
					DCCommand handle = dcCallback;
					if (handle != null) {
						handle.onCommand(msg);
					}

				}
			}
			
			

		}

	}

}
