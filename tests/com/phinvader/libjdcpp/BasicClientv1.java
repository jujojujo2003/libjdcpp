package com.phinvader.libjdcpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import com.phinvader.libjdcpp.UsersHandler.downloadManager;

public class BasicClientv1 {

	public class MyUsersHandler extends UsersHandler implements DCCallback {
		public void onCallback(DCMessage msg, MessageHandler handler) {
			// System.out.println(msg.myinfo.nick);
			return;
		}
	}
	
	public class MyDCRevconnect extends DCRevconnect implements DCCallback{

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

	public class UserInterface {

		UsersHandler user_handler;

		public UserInterface(UsersHandler user_handler) {
			this.user_handler = user_handler;
		}

		public void doUI() {
			while (true) {
				System.out.println("1. Show List of users");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				try {
					String option = br.readLine();
					if (option.equals("1")) {
						Iterator<DCMessage> it = user_handler.nick_q.iterator();
						while (it.hasNext()) {
							System.out.println(it.next().myinfo.nick);
						}
					} else if (option.equals("2")) {
						System.out.println("Enter Nick to get list ");
						String nick_selected = br.readLine();
						// GET LIST
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

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

		try {
			Socket s = new Socket(ipaddress, 411);
			MessageHandler handler = new MessageHandler(s);
			DCMessage lock = handler.getNextMessage();
			DCMessage hubname = handler.getNextMessage();
			DCLogger.Log("Connected to :" + hubname);

			ArrayList<String> supported_methods = new ArrayList<>();
			supported_methods.add("NoHello");
			supported_methods.add("NoGetINFO");

			handler.send_supports(supported_methods);
			handler.send_key(DCFunctions.convert_lock_to_key(lock.lock_s
					.getBytes()));
			
			handler.send_validatenick(nick);
			
			while (true) {
				DCMessage msg = handler.getNextMessage();
				DCLogger.Log(msg.toString());
				if (msg.command != null)
					if (msg.command.equals("Hello")
							&& msg.hello_s.equals(myuser.nick))
						break;
			}
			
			handler.send_version();
			handler.send_getnicklist();
			handler.send_myinfo(myuser);
			
			
			
			BasicClientv1 context = new BasicClientv1();
			
			MessageRouter mr = new MessageRouter(handler);
			DCCommand uh = context.new MyUsersHandler();
			
			
			DCUser target_user = new DCUser();
			target_user.nick = "cracky";
			
			String local_filename = "BOO.xml";
			String remote_filename = "files.xml";
			
			DCCommand revCon = context.new MyDCRevconnect(target_user, myuser, prefs, local_filename, remote_filename);
			handler.send_revconnect(myuser, target_user);

			
			mr.subscribe("MyINFO", uh);
			mr.subscribe("Quit", uh);
			mr.subscribe("ConnectToMe", revCon);
			
			Thread router = new Thread(mr);
			router.start();	
			
			UserInterface uih = context.new UserInterface((UsersHandler) uh);
			uih.doUI();
			
			
			
			
			
			
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
