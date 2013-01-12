package com.phinvader.libjdcpp;

import java.net.Socket;

/**
 * This is the Socket message handler class. Every TCP Connection should be
 * encapsulated within such a Message Handler class. The
 * 
 * @author phinfinity
 * 
 */
public class MessageHandler {
	private Socket tcp_scoket;

	/**
	 * Create a new Message Handler with the Socket s. It spawns a thread to
	 * listen on the Message Queue and buffers incoming messges concurrently
	 * 
	 * @param s
	 */
	public MessageHandler(Socket s) {
		tcp_scoket = s;
		// TODO obtain input/output streams. Start a thread to listen on the
		// input stream for messages and Queues them in a buffer
	}

	/**
	 * This is used to obtain from the Message queue the next message packet
	 * available or null if none exist
	 * 
	 * @return
	 */
	public synchronized DCMessage getNextMessage() {
		// TODO fill code
		return null;
	}

	/**
	 * This function is used to add messages to the queue. This is typically
	 * only used by the parallel thread listening on the socket and handling
	 * messages. As soon as a message is received this function will be called
	 * 
	 * @param msg
	 */
	public synchronized void addMessage(DCMessage msg) {
		// TODO fill code
	}
	
	/**
	 * This function is a means to get updates from the MessageHandler when a new message is received.
	 * @param o
	 * @return
	 */
	public int requestupdates(Object o) {
		// TODO Change this to a listener interface or some better callback design
		return 0;
	}
}
