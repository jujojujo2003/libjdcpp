package com.phinvader.libjdcpp;

import java.net.Socket;
import java.util.concurrent.Semaphore;

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
	}

	/**
	 * This function is a means to get updates from the MessageHandler when a
	 * new message is received. As soon as a message is received the semaphore
	 * registered will be released a single permit to wake up threads waiting on
	 * them.
	 * If a semaphore has already been registered it will be discarded and
	 * replaced with the new one. The old semaphore will no longer receive
	 * permits
	 * 
	 * @see MessageHandler#discardupdates()
	 * @param sem_lock
	 */
	public void requestupdates(Semaphore sem_lock) {
		// TODO add code. Maintain only a single semaphore
	}

	/**
	 * This function is the opposite of
	 * {@link MessageHandler#requestupdates(Semaphore)}. If a semaphore is
	 * registered it will be discarded for future updates.
	 */
	public void discardupdates() {
		// TODO discard code to be added
	}
}
