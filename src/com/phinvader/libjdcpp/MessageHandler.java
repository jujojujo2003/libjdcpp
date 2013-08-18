package com.phinvader.libjdcpp;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
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
	private Semaphore update_sem;
	OutputStream os;
	private final ArrayBlockingQueue<DCMessage> message_queue;
	Thread input_handler_thread;
	private long dump_bytes_limit = 0;
	private String dump_stream_file = null; // Once this is set to not null
											// start dumping the stream to file
	private long dump_bytes = 0;

	/**
	 * Start dumping the rest of the input stream to a file Stop after dumping
	 * no_bytes of data.
	 * 
	 * @param file_name
	 * @param no_bytes
	 */
	public void dump_remaining_stream(String file_name, long no_bytes) {
		dump_stream_file = file_name;
		dump_bytes_limit = no_bytes;
	}

	/**
	 * Get number of bytes dumped to file so far.
	 * 
	 * @return
	 */
	public long get_dumped_bytes() {
		return dump_bytes;
	}

	public long get_filesize() {
		return dump_bytes_limit;
	}

	/**
	 * Create a new Message Handler with the Socket s. It spawns a thread to
	 * listen on the Message Queue and buffers incoming messages concurrently
	 * 
	 * @param s
	 */
	public MessageHandler(final Socket s) {
		tcp_scoket = s;
		message_queue = new ArrayBlockingQueue<DCMessage>(
				DCConstants.max_message_queue_size);
		Runnable input_handler = new Runnable() {
			@Override
			public void run() {
				try {
					InputStream in = s.getInputStream();
					BufferedInputStream bin = new BufferedInputStream(in);
					byte[] buf = new byte[(int) DCConstants.data_chunk_size];
					int buf_read_sz = 0;
					while (true) {
						bin.mark((int) (DCConstants.data_chunk_size + 10));
						buf_read_sz = bin.read(buf);
						if (buf_read_sz == -1)
							break; // EOF
						if (dump_stream_file == null) {
							int o = DCFunctions.find_next(buf, 0, '|');
							byte[] msg = new byte[o];
							System.arraycopy(buf, 0, msg, 0, o);
							addMessage(DCMessage.parse_message(msg));
							bin.reset();
							bin.skip(o + 1);
						} else {
							bin.reset();
							break;
						}
					}
					if (buf_read_sz != -1 && dump_stream_file != null) {
						dump_bytes = 0;
						FileOutputStream output_file = new FileOutputStream(
								dump_stream_file);
						byte[] write_buffer = new byte[DCConstants.io_buffer_size];
						while (true) {
							int s = bin.read(write_buffer);
							if (s == -1)
								break;
							output_file.write(write_buffer, 0, s);
							dump_bytes += s;

							if (dump_bytes >= dump_bytes_limit)
								break;
						}
						output_file.close();
					}
					bin.close();
					addMessage(DCMessage.parse_message("$HubQuit".getBytes()));
				} catch (Exception e) { // IOException InterruptedException
					try {
						addMessage(DCMessage.parse_message(("$HubQuit " + e
								.toString()).getBytes()));
						// s.close();
						// TODO remove this stacktrace. Added only for debugging
						// purposes
						e.printStackTrace();
					} catch (InterruptedException e1) {
					}
				}
			}
		};
		input_handler_thread = new Thread(input_handler);
		input_handler_thread.start();

		// Just get the output stream
		try {
			os = s.getOutputStream();
		} catch (IOException e) {
			try {
				addMessage(DCMessage.parse_message("$HubQuit".getBytes()));
			} catch (InterruptedException e1) {
			}
		}
	}

	/**
	 * Interrupts the input thread and tries to close the socket connection
	 */
	public void close() {
		try {
			tcp_scoket.close();
		} catch (IOException e) {
		}
		input_handler_thread.interrupt();
	}

	/**
	 * This is used to obtain from the Message queue the next message packet
	 * available or blocks if none exist. (Uses java inbuilt blocking queue)
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public DCMessage getNextMessage() throws InterruptedException {
		return message_queue.take();
	}

	public DCMessage checkNextMessage() {
		return message_queue.peek();
	}

	public boolean isEmpty() {
		return message_queue.isEmpty();
	}

	/**
	 * (Do Not use this outside class as public member unless explicitly needed.
	 * messages added are not sent over the socket)
	 * 
	 * This function is used to add messages to the queue. This is typically
	 * only used by the parallel thread listening on the socket and handling
	 * messages. As soon as a message is received this function will be called
	 * 
	 * NOTE: This function will block calling thread if the Mes$Lock
	 * EXTENDEDPROTOCOLABCABCABCABCABCABC Pk=py-dchub-0.2.4--|$HubName
	 * SuperNova|sage Queue is full and will remain blocked until the message
	 * Queue is free. DCConstants.max_message_queue_size specifies the Maximum
	 * Queue size
	 * 
	 * if updates requested on a semaphore , semaphore will be released one
	 * permit
	 * 
	 * @param msg
	 * @throws InterruptedException
	 */
	public void addMessage(DCMessage msg) throws InterruptedException {
		message_queue.put(msg);
		if (update_sem != null)
			update_sem.release();
	}

	/**
	 * This function is a means to get updates from the MessageHandler when a
	 * new message is received. As soon as a message is received the semaphore
	 * registered will be released a single permit to wake up threads waiting on
	 * them. If a semaphore has already been registered it will be discarded and
	 * replaced with the new one. The old semaphore will no longer receive
	 * permits
	 * 
	 * @see MessageHandler#discardupdates()
	 * @param sem_lock
	 */
	public void requestupdates(Semaphore sem_lock) {
		update_sem = sem_lock;
	}

	/**
	 * $Lock EXTENDEDPROTOCOLABCABCABCABCABCABC Pk=py-dchub-0.2.4--|$HubName
	 * SuperNova| This function is the opposite of
	 * {@link MessageHandler#requestupdates(Semaphore)}. If a semaphore is
	 * registered it will be discarded for future updates.
	 */
	public void discardupdates() {
		update_sem = null;
	}

	public synchronized void send_msg(String s) {
		s += "|";
		try {
			os.write(s.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	public void send_supports(List<String> sup) {
		String msg = "$Supports ";
		for (String s : sup) {
			msg += s + " ";
		}
		send_msg(msg);
	}

	public void send_key(byte[] key) {
		String msg = "$Key ";
		// msg += new String(key, StandardCharsets.ISO_8859_1);
		try {
			msg += new String(key, "ISO_8859_1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		send_msg(msg);
	}

	public void send_validatenick(String nick) {
		String msg = "$ValidateNick " + nick;
		send_msg(msg);
	}

	public void send_version() {
		send_msg("$Version 1,0091");
	}

	public void send_getnicklist() {
		send_msg("$GetNickList");
	}

	public void send_mynick(DCUser user) {
		send_msg("$MyNick " + user.nick);
	}

	public void send_lock() {
		String s = "$Lock " + DCConstants.default_lock + " Pk="
				+ DCConstants.default_pk;
		send_msg(s);
	}

	public void send_direction(boolean download) {
		int random_v = (int) (Math.random() * 65535);
		String dir = "Download";
		if (!download)
			dir = "Upload";
		send_msg("$Direction " + dir + " " + random_v);
	}

	public void send_revconnect(DCUser mynick, DCUser connecting_nick) {
		send_msg("$RevConnectToMe " + mynick.nick + " " + connecting_nick.nick);
	}

	public void send_connecttome(DCUser mynick, String host, int port) {
		send_msg("$ConnectToMe " + mynick.nick + " " + host + ":"
				+ Integer.toString(port));
	}

	public void send_search(String searchString, DCUser myuser) {
		send_msg("$Search Hub:" + myuser.nick + " " + searchString);
	}

	public void send_myinfo(DCUser user) {
		DCUser myuser = new DCUser(user);
		if (myuser.description == null)
			myuser.description = " ";
		if (myuser.tag == null) {
			myuser.tag = "<++ V:" + DCConstants.version_short + ","
					+ (myuser.active ? "M:A" : "M:P") + "," + "H:1/0/0,S:3>";
		}
		if (myuser.connection_speed == null) {
			myuser.connection_speed = "1";
		}
		if (myuser.email == null) {
			myuser.email = "";
		}
		String msg = "$MyINFO $ALL ";
		msg += myuser.nick;
		msg += myuser.description + " " + myuser.tag + "$ $";
		msg += myuser.connection_speed;
		msg += (char) myuser.speed_id;
		msg += "$" + myuser.email + "$";
		msg += Long.toString(myuser.share_size) + "$";
		send_msg(msg);
	}
}
