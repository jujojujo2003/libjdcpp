package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;

import com.phinvader.libjdcpp.DCRevconnect.DownloadStatus;

public class DCDownloader {

	private MessageHandler handler ;
	
	public long getDownloadStatus(){
		return handler.get_dumped_bytes();
	}
	public long getDownloadFileFullSize(){
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

	private boolean download_file(DCUser myuser, Socket s, String fname,
			String save_file_name) {
		// Using REVCONNECT
		try {
			DCLogger.Log("Download Started" + s.toString());

			handler = new MessageHandler(s);
			handler.send_mynick(myuser);
			handler.send_lock();

			int NUMBER_OF_EXPECTED_REPLIES_FROM_SERVER = 4;
			// Nick, Lock, Direction, Key.

			DCMessage hisnick = null;
			DCMessage rlock = null;
			DCMessage key = null;
			DCMessage direction = null;
			for (int i = 0; i < NUMBER_OF_EXPECTED_REPLIES_FROM_SERVER;) {
				DCMessage msg = handler.getNextMessage();
				if (msg.command != null) {
					i++;
					if (msg.command.equals("MyNick")) {
						hisnick = msg;
					}
					if (msg.command.equals("Lock")) {
						rlock = msg;
					}
					if (msg.command.equals("Direction")) {
						direction = msg;
					}
					if (msg.command.equals("Key")) {
						key = msg;
					}
				}
			}

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
			s.close();
			handler.close();
			DCLogger.Log("DOwnload Complete");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	
	/**
	 * Download Manager will do the downloading in a separate thread to keep the UI Responsive
	 * @author madhavan
	 *
	 */
	
	public class downloadManager implements Runnable {

		private Socket s;
		private DCRevconnect revCon;
		private DCUser myuser;
		private String remote_filename;
		private String local_filename;

		public downloadManager(DCRevconnect revCon, DCUser myuser, String remote_filename,
				String local_filename) {
			super();
			this.revCon = revCon;
			this.s = revCon.s;
			this.myuser = myuser;
			this.remote_filename = remote_filename;
			this.local_filename = local_filename;
		}

		@Override
		public void run() {
			if(download_file(myuser, s, remote_filename, local_filename)){
				revCon.setCurrentDownloadStatus(DownloadStatus.COMPLETED);
			}
			else{
				revCon.setCurrentDownloadStatus(DownloadStatus.INTERUPTED);
			}

		}

	}

	
	
}
