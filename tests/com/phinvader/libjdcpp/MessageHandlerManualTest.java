package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
		try {
			Socket s = new Socket(host,port);
			MessageHandler handler = new MessageHandler(s);
			DCMessage lock = handler.getNextMessage();
			DCMessage hubname = handler.getNextMessage();
			System.out.println("Connected to Hub : " + hubname.hubname_s);
			s.close();
			handler.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
