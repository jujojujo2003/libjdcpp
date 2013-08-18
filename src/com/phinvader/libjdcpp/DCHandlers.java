package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DCHandlers {

	public static interface Downloader {
		public String remote_filename = null;
		public String local_filename = null;

	}

	public static class MainDownloadHandler implements DCCommand {

		private DCUser my_user;
		private DCClient client;
		private DCDownloader downloaderManager;

		public MainDownloadHandler(DCUser my_user, DCDownloader downloader,
				DCClient client) {
			super();
			this.my_user = my_user;
			this.client = client;
			this.downloaderManager = downloader;
		}

		@Override
		public void onCommand(DCMessage msg) {
			DCLogger.Log("in DCRevconnect" + msg.toString());
			String target_identifier = msg.toString().split(" ")[3];
			String[] ip_port_raw = target_identifier.split(":");
			String remote_ip = ip_port_raw[0];
			String remote_port = ip_port_raw[1];

			DCClient downloader = new DCClient();

			DCMessage hisnick = null;
			DCMessage rlock = null;
			DCMessage key = null;
			DCMessage direction = null;

			MessageHandler handlerLocal = null;
			try {
				Socket s = new Socket(remote_ip, Integer.parseInt(remote_port));
				handlerLocal = new MessageHandler(s);

				handlerLocal.send_mynick(my_user);
				handlerLocal.send_lock();
				for (int i = 0; i < 4;) {
					DCMessage msg2 = handlerLocal.getNextMessage();
					if (msg2.command != null) {
						i++;
						if (msg2.command.equals("MyNick")) {
							DCLogger.Log("GOT MYNICK");
							hisnick = msg2;
						}
						if (msg2.command.equals("Lock")) {
							rlock = msg2;
						}
						if (msg2.command.equals("Direction")) {
							direction = msg2;
						}
						if (msg2.command.equals("Key")) {
							key = msg2;
						}
					}
				}

			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				DCLogger.Log("ERROR - Socket creation error 1");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				DCLogger.Log("ERROR - Socket creation error 2");
				e.printStackTrace();
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			DCDownloader.DownloadQueueEntity downloadEntity = downloaderManager
					.getDownloadEntity(hisnick.hisinfo.nick);
			if (downloadEntity == null)
				return;

			downloader.startDownloadingFile(downloadEntity, client, rlock,
					handlerLocal);

		}
	}

	public static class UnsubscriptionHandler implements Runnable {

		private DCClient client;
		private DCCommand handler;
		private int timeout;

		public UnsubscriptionHandler(DCClient client, DCCommand handler,
				int timeout) {
			super();
			this.client = client;
			this.handler = handler;
			this.timeout = timeout;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.unsetPassiveDownloadHandler(handler);

		}

	}

	public static class BoardMessageHandler implements DCCommand {

		List<DCMessage> listOfMessages = new ArrayList<DCMessage>();

		@Override
		public void onCommand(DCMessage msg) {
			listOfMessages.add(msg);
		}

		public List<DCMessage> getLatestMessages() {
			return getLatestMessages(100);
		}

		public List<DCMessage> getLatestMessages(int limit) {
			int lowerBound = listOfMessages.size() - 1 - limit;
			int upperBound = listOfMessages.size() - 1;
			if (limit > listOfMessages.size()) {
				lowerBound = 0;
			}
			return listOfMessages.subList(lowerBound, upperBound);

		}

		public String toString() {
			int size = listOfMessages.size();
			String logString = "Size : "
					+ Integer.toString(listOfMessages.size());
			if (size > 5) {
				logString += "..." + listOfMessages.get(size - 4) + ","
						+ listOfMessages.get(size - 3) + ","
						+ listOfMessages.get(size - 2) + ","
						+ listOfMessages.get(size - 1);
			}
			return logString;
		}

	}

}
