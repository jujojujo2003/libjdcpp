package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MessageHandlerManualTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String host = args[0];
		int port = 411;
		if (args.length > 1)
			port = Integer.parseInt(args[1]);
		DCUser myuser = new DCUser();
		myuser.nick = "libjdcpptest";
		try {
			Socket s = new Socket(host, port);
			MessageHandler handler = new MessageHandler(s);
			DCMessage lock = handler.getNextMessage();
			System.out.println(lock.toString());
			DCMessage hubname = handler.getNextMessage();
			System.out.println(hubname.toString());
			ArrayList<String> sup_list = new ArrayList<>();
			sup_list.add("NoGetINFO");
			sup_list.add("NoHello");
			handler.send_supports(sup_list);
			handler.send_key(DCFunctions.convert_lock_to_key(lock.lock_s
					.getBytes()));
			handler.send_validatenick(myuser.nick);
			while (true) {
				DCMessage msg = handler.getNextMessage();
				System.out.println(msg.toString());
				if (msg.command != null)
					if (msg.command.equals("Hello")
							&& msg.hello_s.equals(myuser.nick))
						break;
			}
			handler.send_version();
			handler.send_getnicklist();
			handler.send_myinfo(myuser);

			DCUser dummy_user = new DCUser();
			dummy_user.nick = "asd3";
			handler.send_revconnect(myuser, dummy_user);
			
			while (true) {
				DCMessage msg = handler.getNextMessage();
				if (msg.command != null && msg.command.equals("HubQuit"))
					break;
				System.out.println(msg.toString());
			}
			s.close();
			handler.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
