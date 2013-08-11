package com.phinvader.libjdcpp;

import java.io.IOException;

public class BasicClientv2 {
	
	private class MyUsersHandler extends DCClient.NotifyUsersChange implements DCCallback{

		@Override
		public void onCallback(DCMessage msg) {
			System.out.println("CALLBACK TIME : "+msg.toString());
		}
				
	}
	
	
	public class MySearchHandler extends DCClient.BasicCallbackHandler implements DCCallback{

		@Override
		public void onCallback(DCMessage msg) {
			DCLogger.Log(msg.hisinfo.nick+":::"+msg.file_path.split("\\x005")[0]);
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
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		client.InitiateDefaultRouting();
		BasicClientv2 context = new BasicClientv2();
		MyUsersHandler uh = context.new MyUsersHandler();
		MySearchHandler sh = context.new MySearchHandler();
		client.setSearchHandler(sh);
		client.setUserChangeHandler(uh);
		
		DCUser target_user = new DCUser();
		target_user.nick = "Anecdote";
		
		String local_filename = "Boo2.xml";
		String remote_filename = "files.xml";
		
		//MyDCRevconnect myrc = context.new MyDCRevconnect(target_user, myuser, prefs, local_filename, remote_filename);
		DCClient.PassiveDownloadConnection myrc= new DCClient.PassiveDownloadConnection(target_user, myuser, prefs, local_filename, remote_filename);
		client.setPassiveDownloadHandler(target_user, myuser, myrc);

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
