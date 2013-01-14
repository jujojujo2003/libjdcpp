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
	public DCMessage getNextMessage() {
		// TODO fill code
		// TODO Ensure concurrent thread access to Message Queue
		return null;
	}

	/**
	 * This function is used to add messages to the queue. This is typically
	 * only used by the parallel thread listening on the socket and handling
	 * messages. As soon as a message is received this function will be called
	 * 
	 * NOTE: This function will block calling thread if the Message Queue is
	 * full and will remain blocked until the message Queue is free.
	 * DCConstants.max_message_queue_size specifies the Maximum Queue size
	 * 
	 * @param msg
	 */
	public void addMessage(DCMessage msg) {
		// TODO fill code
		// TODO Ensure concurrent thread access with Queue size block
		// Avoid Polling where possible
	}

	/**
	 * This function is a means to get updates from the MessageHandler when a
	 * new message is received. As soon as a message is received all monitors
	 * registered will be .notify() in order to wake up threads waiting on them.
	 * In spite of this it is advised to wait() with a timeout of say 5 seconds to
	 * avoid deadlocks. Deadlock scenarios is possible where a thread gets the
	 * notify() before it calls wait() and hence regular polling is required
	 * 
	 * @param monitor
	 * @return
	 */
	public int requestupdates(Object monitor) {
		// TODO Change this to a listener interface if it would be better.
		return 0;
	}
}
