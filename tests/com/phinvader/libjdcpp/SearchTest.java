package com.phinvader.libjdcpp;

import java.io.IOException;

public class SearchTest {

	private class MySearchHandler implements DCCommand {

		@Override
		public void onCommand(DCMessage msg) {
			 DCLogger.Log("SEARCH " + msg.file_path + " PATH : "+msg.hisinfo);
		}

	}

	public static void main(String[] args) {

		DCPreferences prefs = new DCPreferences("libjdcpp_user2", 3000L*1024*1024,
				"10.2.4.22");// This is a dummy preferences saver. TODO :
								// Replace saver with a persistent storage

		String nick = prefs.getNick();
		String ipaddress = prefs.getServer_ip();

		DCUser myuser = new DCUser();
		myuser.nick = nick;

		DCClient client = new DCClient();
		try {
			client.connect("10.2.4.22", 411, prefs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.bootstrap(myuser);
		SearchTest context = new SearchTest();
		client.InitiateDefaultRouting();

		MySearchHandler srch = context.new MySearchHandler();
		client.setCustomSearchHandler(srch);
		// getBoardMessages() gets last 100 messages
		// getBoardMessages(500) gets last 500 messages
		// Return is a List<DCMessage>
		// each entry in the list, msg, has msg.command=BoardMessage, and
		// message string stored in msg.msg_s
		client.searchForFile("dexter", myuser, DCConstants.FILETYPE_ANY);
		DCLogger.Log("Done");
	}
}
