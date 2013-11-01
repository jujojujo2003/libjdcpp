package com.phinvader.libjdcpp;

import java.util.ArrayList;
import java.util.HashMap;

import com.phinvader.libjdcpp.DCConstants.DownloadStatus;

public class DCDownloader {
	
	public static class DownloadQueueEntity{
		public DCUser target_user;
		public DCUser my_user;
		public String remote_filename;
		public String local_filename;
		public MessageHandler connectionHandler;
		public DownloadQueueEntity(DCUser target_user, DCUser my_user,
				String remote_filename, String local_filename) {
			super();
			this.target_user = target_user;
			this.my_user = my_user;
			this.remote_filename = remote_filename;
			this.local_filename = local_filename;
		}

		public Thread thread;
		public long downloadedSize = 0;
		public long expectedDownloadSize = 0;
		public DCConstants.DownloadStatus status;

	}

	public HashMap<String, ArrayList<DownloadQueueEntity>> downloadQ;

	public void initDownloadQ() {
		this.downloadQ = new HashMap<String, ArrayList<DownloadQueueEntity>>();
	}

	public void addDownloadEntity(String nick , DownloadQueueEntity entity) {
		ArrayList<DownloadQueueEntity> list = downloadQ.get(nick);
		if(list == null){
			list = new ArrayList<DCDownloader.DownloadQueueEntity>();
		}
		list.add(entity);
		downloadQ.put(nick, list);
	}

	public DownloadQueueEntity getDownloadEntity(String nick) {
		if(downloadQ == null)
			return null;
		if(downloadQ.get(nick)==null)
			return null;
		DownloadQueueEntity ret = downloadQ.get(nick).get(0);
		downloadQ.get(nick).remove(ret);
		return ret;
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
	//
	private boolean download_file(DownloadQueueEntity entity, DCMessage rlock,
			MessageHandler handler) {
		// Using REVCONNECT
		String fname = entity.remote_filename;
		String save_file_name = entity.local_filename;
		entity.connectionHandler = handler;
		try {
			// At this point Handler has been associated with a socket
			// MyNick and Lock have been sent.
			// Send Direction and Key

			handler.send_direction(true);
			handler.send_key(DCFunctions.convert_lock_to_key(rlock.lock_s
					.getBytes()));

			// Notify target_user about file to download
			handler.send_msg("$Get " + fname + "$1");
			DCMessage msg2 = handler.getNextMessage();

			// Request Start Download
			handler.send_msg("$Send");
			entity.status = DownloadStatus.INITIATED;

			if (msg2.command == null || !msg2.command.equals("FileLength")) {
				entity.status = DownloadStatus.FAILED;
				handler.close();
				return false;
			}

			// Update filesize
			entity.expectedDownloadSize = msg2.file_length;

			// Associate ENTITY to HANDLER to monitor progress
			// Done esp. for downloadedSize
			handler.setDownloadEntity(entity);

			// Download Started
			entity.status = DownloadStatus.STARTED;
			handler.dump_remaining_stream(save_file_name, msg2.file_length);
			while (true) {
				entity.status = DownloadStatus.DOWNLOADING;
				DCMessage msg = handler.getNextMessage();
				if (msg.command != null && msg.command.equals("HubQuit"))
					break;
			}

			// Download COMPLETE
			handler.close();
			entity.status = DownloadStatus.COMPLETED;
			DCLogger.Log("Download Complete" + save_file_name
					+ entity.status.toString());

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

		private DCMessage rlock;
		private MessageHandler messageHandler;
		private DownloadQueueEntity requestEntity;

		public downloadManager(DownloadQueueEntity requestEntity,
				DCMessage rlock,
				MessageHandler handler) {
			super();
			this.rlock = rlock;
			this.messageHandler = handler;
			this.requestEntity = requestEntity;
		}

		@Override
		public void run() {
			download_file(requestEntity, rlock, messageHandler);

		}

	}

}
