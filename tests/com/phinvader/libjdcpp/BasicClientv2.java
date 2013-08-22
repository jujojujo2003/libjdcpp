package com.phinvader.libjdcpp;

import java.io.IOException;

public class BasicClientv2 {

	private class MyUserHandler implements DCCommand {

		@Override
		public void onCommand(DCMessage msg) {
			// DCLogger.Log("CALLBACK TIME: " + msg.toString());
		}
	}

	private class MyBoardMessageHandler implements DCCommand {

		@Override
		public void onCommand(DCMessage msg) {
			// DCLogger.Log("A MESSAGE! "+msg.msg_s);
		}

	}
	private class MySearchHandler implements DCCommand {

		@Override
		public void onCommand(DCMessage msg) {
			// DCLogger.Log("SEARCH " + msg.file_path);
		}

	}

	public static void main(String[] args) {
		DCPreferences prefs = new DCPreferences("libjdcpp_user2", 1000,
				"10.2.16.126");// This is a dummy preferences saver. TODO :
								// Replace saver with a persistent storage

		String nick = prefs.getNick();
		String ipaddress = prefs.getServer_ip();

		DCUser myuser = new DCUser();
		myuser.nick = nick;

		DCClient client = new DCClient();
		try {
			client.connect("10.2.16.126", 411, prefs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.bootstrap(myuser);
		BasicClientv2 context = new BasicClientv2();

		MyUserHandler uh = context.new MyUserHandler();
		client.setCustomUserChangeHandler(uh);
		DCCommand messageHandler = context.new MyBoardMessageHandler();
		client.setCustomBoardMessageHandler(messageHandler);
		client.InitiateDefaultRouting();

		String remote_filename = "files.xml";
		DCUser target_user = new DCUser();
		target_user.nick = "cracky";

		String local_filename = "Boo2.xml";

		DCClient.PassiveDownloadConnection myrc = new DCClient.PassiveDownloadConnection(
				target_user, myuser, prefs, local_filename, remote_filename,
				client);
		DCDownloader.DownloadQueueEntity e3 = null;
		try {
			e3 = client.startPassiveDownload(myrc, 1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DCLogger.Log(Long.toString(e3.downloadedSize));
		DCLogger.Log(Long.toString(e3.expectedDownloadSize));

		DCUser target_user2 = new DCUser();
		target_user2.nick = "cracky";

		DCClient.PassiveDownloadConnection myrc2 = new DCClient.PassiveDownloadConnection(
				target_user2, myuser, prefs, "Boo3.xml", remote_filename,
				client);

		try {
			e3 = client.startPassiveDownload(myrc2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DCLogger.Log("Time out completed");

		MySearchHandler srch = context.new MySearchHandler();
		client.setCustomSearchHandler(srch);
		// getBoardMessages() gets last 100 messages
		// getBoardMessages(500) gets last 500 messages
		// Return is a List<DCMessage>
		// each entry in the list, msg, has msg.command=BoardMessage, and
		// message string stored in msg.msg_s
		client.searchForFile("F?T?0?1?dexter", myuser);

	}

}
