package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MessageHandlerManualClientClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String host = args[0];
		int port = 411;
		if (args.length > 1)
			port = Integer.parseInt(args[1]);
		String fname="files.xml";
		if (args.length > 2)
			fname = args[2];
		DCUser myuser = new DCUser();
		myuser.nick = "libjdcpptest";
		try {
			Socket s = new Socket(host, port);
			MessageHandler handler = new MessageHandler(s);
			handler.send_mynick(myuser);
			handler.send_lock();
			DCMessage rlock = null;
			DCMessage rdir = null;
			DCMessage rkey = null;
			for (int i = 0; i < 4; i++) {
				DCMessage msg = handler.getNextMessage();
				System.out.println("Got : " + msg.command);
				if (msg.command != null) {
					if (msg.command.equals("Lock"))
						rlock = msg;
					else if (msg.command.equals("Direction"))
						rdir = msg;
					else if (msg.command.equals("Key"))
						rkey = msg;
				}
			}
			if (!Arrays.equals(rkey.key_s.getBytes(), DCFunctions
					.convert_lock_to_key(DCConstants.default_lock.getBytes())))
				System.err
						.println("Key no match!! but still proceeding\nExpection : "
								+ new String(
										DCFunctions
												.convert_lock_to_key(DCConstants.default_lock
														.getBytes())));
			handler.send_direction(true);
			handler.send_key(DCFunctions.convert_lock_to_key(rlock.lock_s
					.getBytes()));
			handler.send_msg("$Get "+fname+"$1");
			DCMessage msg2 = handler.getNextMessage();
			System.out.println(msg2.toString());
			handler.send_msg("$Send");
			while (true) {
				DCMessage msg = handler.getNextMessage();
				System.out.println("Got Message");
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
