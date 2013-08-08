package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class BasicClientv1 {
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
			supported_methods.add("noGetINFO");

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
			
			ArrayBlockingQueue<String> nick_q = new ArrayBlockingQueue<>(1024);
			nick_q = UsersHandler.getUsers(handler);
			
			System.out.println("======>"+nick_q.toString());
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
