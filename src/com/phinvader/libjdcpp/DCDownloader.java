package com.phinvader.libjdcpp;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class DCDownloader {
	private MessageHandler handler;
	
	public static class DownloadQueueEntity{
		public DCUser target_user;
		public DCUser my_user;
		public String remote_filename;
		public String local_filename;
		public DownloadQueueEntity(DCUser target_user, DCUser my_user,
				String remote_filename, String local_filename) {
			super();
			this.target_user = target_user;
			this.my_user = my_user;
			this.remote_filename = remote_filename;
			this.local_filename = local_filename;
		}

	}

	public HashMap<String, ArrayList<DownloadQueueEntity>> downloadQ;

	public void initDownloadQ() {
		this.downloadQ = new HashMap<>();
	}

	public void addDownloadEntity(String nick , DownloadQueueEntity entity) {
		ArrayList<DownloadQueueEntity> list = downloadQ.get(nick);
		if(list == null){
			list = new ArrayList<>();
		}
		list.add(entity);
		downloadQ.put(nick, list);
	}

	public DownloadQueueEntity getDownloadEntity(String nick) {
		DownloadQueueEntity ret = downloadQ.get(nick).get(0);
		downloadQ.remove(ret);
		return ret;
	}

	public int CHECKOUT_TIME_INTERVAL = 10; // Polling Next message, for race
											// condition

	public long getDownloadStatus() {
		return handler.get_dumped_bytes();
	}

	public long getDownloadFileFullSize() {
		return handler.get_filesize();
	}

	/**
	 * 
	 * @param nick
	 *            - Nick to download from
	 * @param s
	 *            - Socket connection
	 * @param remote_filename
	 *            - Path to the file on remote user, from where to be downloaded
	 * @param local_filename
	 *            - Path where the downloaded file should be saved
	 * @return
	 */

	private boolean download_file(DCUser myuser, DCUser target_user, Socket s,
			String fname, String save_file_name, DCMessage rlock,
			MessageHandler handler) {
		// Using REVCONNECT
		this.handler = handler;
		try {

			// handler = new MessageHandler(s);
			// handler.send_mynick(myuser);
			// handler.send_lock();

			int NUMBER_OF_EXPECTED_REPLIES_FROM_SERVER = 4;
			// Nick, Lock, Direction, Key.

			// DCMessage hisnick = null;
			// DCMessage rlock = null;
			// DCMessage key = null;
			// DCMessage direction = null;

			// DCMessage litmusMessage = null;
			// // Keep polling till there is a message in the queue
			// while (litmusMessage == null) {
			// litmusMessage = handler.checkNextMessage();
			// Thread.sleep(CHECKOUT_TIME_INTERVAL);
			// }
			//
			// // First message should be MyNick == target_user
			//
			// if (litmusMessage.command.equals("MyNick")) {
			// if (!litmusMessage.hisinfo.nick.equals(target_user.nick)) {
			// DCLogger.Log("Race condition detected, Evaded.");
			// return false;
			// } else {
			DCLogger.Log("Download Started" + save_file_name);
			// }
			// }
			// for (int i = 0; i < NUMBER_OF_EXPECTED_REPLIES_FROM_SERVER;) {
			// DCMessage msg = handler.getNextMessage();
			// if (msg.command != null) {
			// i++;
			// if (msg.command.equals("MyNick")) {
			// hisnick = msg;
			// }
			// if (msg.command.equals("Lock")) {
			// rlock = msg;
			// }
			// if (msg.command.equals("Direction")) {
			// direction = msg;
			// }
			// if (msg.command.equals("Key")) {
			// key = msg;
			// }
			// }
			// }

			handler.send_direction(true);
			handler.send_key(DCFunctions.convert_lock_to_key(rlock.lock_s
					.getBytes()));

			handler.send_msg("$Get " + fname + "$1");
			DCMessage msg2 = handler.getNextMessage();
			handler.send_msg("$Send");

			if (msg2.command == null || !msg2.command.equals("FileLength")) {
				DCLogger.Log("Quitting..");
				handler.close();
				return false;
			}
			handler.dump_remaining_stream(save_file_name, msg2.file_length);
			while (true) {
				DCMessage msg = handler.getNextMessage();
				if (msg.command != null && msg.command.equals("HubQuit"))
					break;
			}
			// s.close();
			handler.close();
			DCLogger.Log("DOwnload Complete" + save_file_name);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Download Manager will do the downloading in a separate thread to keep the
	 * UI Responsive
	 * 
	 * @author madhavan
	 * 
	 */

	public class downloadManager implements Runnable {

		private Socket s;
		private DCRevconnect revCon;
		private DCUser myuser;
		private String remote_filename;
		private String local_filename;
		private DCUser target_user;
		private DCClient client;
		private DCMessage rlock;
		private MessageHandler messageHandler;

		public downloadManager(DCUser myuser,
				DCUser target_user, String remote_filename,
 String local_filename, DCMessage rlock,
				MessageHandler handler) {
			super();
			// this.revCon = revCon;
			// this.s = revCon.s;
			this.myuser = myuser;
			this.remote_filename = remote_filename;
			this.local_filename = local_filename;
			this.target_user = target_user;
			this.client = client;
			this.rlock = rlock;
			this.messageHandler = handler;
		}

		@Override
		public void run() {
			if (download_file(myuser, target_user, s, remote_filename,
					local_filename, rlock, messageHandler)) {
				// revCon.setCurrentDownloadStatus(DownloadStatus.COMPLETED);
			} else {
				// revCon.setCurrentDownloadStatus(DownloadStatus.INTERUPTED);
			}

		}

	}

}
