package com.phinvader.libjdcpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class BasicClientv1 {
	
	
	public class UserInterface implements Runnable{

		public UserInterface(){
			
		}
		@Override
		public void run() {
			while(true){
				System.out.println("1. Show List of users");
				BufferedReader br  = new BufferedReader(new InputStreamReader(System.in));
				try {
					String option = br.readLine();
					if(option.equals("1")){
						Iterator<DCMessage> it = UsersHandler.nick_q.iterator();
						while(it.hasNext()){
							System.out.println(it.next().myinfo.nick);
						}
					}
					else if(option.equals("2")){
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

		DCPreferences prefs = new DCPreferences("libjdcpp_user1", 1000,
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
			
			
			
			// All activities to be done as threads.
			BasicClientv1 context = new BasicClientv1();
			Thread uiThread = new Thread(context.new UserInterface());
			uiThread.start();
			
			
			// Captures packets and redirects to the appropriate handler.			
			MessageRouter.startRouting(handler);
			
			
			
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
