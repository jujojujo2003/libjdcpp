package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.UnknownHostException;

import com.phinvader.libjdcpp.UsersHandler.downloadManager;

public class BasicClientv2 {
	
	private class MyUsersHandler extends DCClient.NotifyUsersChange implements DCCallback{

		@Override
		public void onCallback(DCMessage msg, MessageHandler handler) {
			System.out.println("CALLBACK TIME : "+msg.toString());
		}
				
	}
	
	
	
	public class MyDCRevconnect extends DCClient.PassiveDownloadConnection implements DCCallback{

		private DCUser target_user;
		private DCUser my_user;
		private DCPreferences prefs;
		private String local_filename;
		private String remote_filaname;
		
		
		
		public MyDCRevconnect(DCUser target_user, DCUser my_user,
				DCPreferences prefs, String local_filename,
				String remote_filaname) {
			super();
			this.target_user = target_user;
			this.my_user = my_user;
			this.prefs = prefs;
			this.local_filename = local_filename;
			this.remote_filaname = remote_filaname;
		}



		@Override
		public void onCallback(DCMessage msg, MessageHandler handler) {
			//Start download once connection is established.
			UsersHandler u = new UsersHandler();
			DCLogger.Log("DCREV Callback");
			UsersHandler.downloadManager dm = u.new downloadManager(s, my_user, remote_filaname, local_filename);
			Thread dm_thread = new Thread(dm);
			dm_thread.start();
			
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
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		client.InitiateDefaultRouting();
		BasicClientv2 context = new BasicClientv2();
		MyUsersHandler uh = context.new MyUsersHandler();
		client.setUserChangeHandler(uh);
		
		DCUser target_user = new DCUser();
		target_user.nick = "Anecdote";
		
		String local_filename = "Boo2.xml";
		String remote_filename = "files.xml";
		
		MyDCRevconnect myrc = context.new MyDCRevconnect(target_user, myuser, prefs, local_filename, remote_filename);
		client.setPassiveDownloadHandler(target_user, myuser, myrc);
		
		
		
		
	}

}
