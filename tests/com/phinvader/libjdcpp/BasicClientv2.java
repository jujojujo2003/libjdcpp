package com.phinvader.libjdcpp;

import java.io.IOException;

public class BasicClientv2 {
	
	private class MyUserHandler implements DCCommand{

		@Override
		public void onCommand(DCMessage msg) {
			DCLogger.Log("CALLBACK TIME: "+msg.toString());
		}
	}
	
	private class MyBoardMessageHandler implements DCCommand{

		@Override
		public void onCommand(DCMessage msg) {
			DCLogger.Log("A MESSAGE! "+msg.msg_s);
		}
		
	}
	

	
	public static void main(String[] args)  {
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
		client.bootstrap();
		BasicClientv2 context = new BasicClientv2();
		
		MyUserHandler uh = context.new MyUserHandler();
		client.setCustomUserChangeHandler(uh);
		DCCommand messageHandler = context.new MyBoardMessageHandler();
		client.setCustomBoardMessageHandler(messageHandler);
		
		DCUser target_user = new DCUser();
		target_user.nick = "Anecdote";
		
		String local_filename = "Boo2.xml";
		String remote_filename = "files.xml";
		
		DCClient.PassiveDownloadConnection myrc= new DCClient.PassiveDownloadConnection(target_user, myuser, prefs, local_filename, remote_filename);
		client.setPassiveDownloadHandler(target_user, myuser, myrc);
		client.InitiateDefaultRouting();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		DCLogger.Log("DOWNLOAD SOFAR : "+Long.toString(myrc.getDownloadBytes()));
		DCLogger.Log("DOWNLOAD TOTAL EXPECTED: "+Long.toString(myrc.getDownloadFileFullSize()));
		//client.searchForFile("F?T?0?1?dexter", myuser);
		
		
		
		
	}

}
