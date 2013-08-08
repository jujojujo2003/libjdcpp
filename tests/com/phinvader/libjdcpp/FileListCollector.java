package com.phinvader.libjdcpp;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

public class FileListCollector {

	private class CollectorWorker implements Runnable {
		private MessageHandler handler;
		private ArrayBlockingQueue<String> nick_list;

		private void download_file(Socket s, String fname, String save_file_name) {
			DCUser myuser = new DCUser();
			myuser.nick = "libjdcpptest";
			try {
				MessageHandler handler = new MessageHandler(s);
				DCMessage hisnick = handler.getNextMessage();
				DCMessage rlock = handler.getNextMessage();
				handler.send_mynick(myuser);
				handler.send_lock();
				handler.send_direction(true);
				handler.send_key(DCFunctions.convert_lock_to_key(rlock.lock_s
						.getBytes()));
				DCMessage rdir = null;
				DCMessage rkey = null;
				for (int i = 0; i < 2; i++) {
					DCMessage msg = handler.getNextMessage();
					if (msg.command != null) {
						if (msg.command.equals("Lock"))
							rlock = msg;
						else if (msg.command.equals("Direction"))
							rdir = msg;
						else if (msg.command.equals("Key"))
							rkey = msg;
						else if (msg.command.equals("HubQuit"))
							return;
					}
				}
				handler.send_msg("$Get " + fname + "$1");
				DCMessage msg2 = handler.getNextMessage();
				if (msg2.command == null || !msg2.command.equals("FileLength")) {
					DCLogger.Log("Quitting..");
					handler.close();
					return;
				}
				handler.dump_remaining_stream(save_file_name, msg2.file_length);
				handler.send_msg("$Send");
				while (true) {
					DCMessage msg = handler.getNextMessage();
					if (msg.command != null && msg.command.equals("HubQuit"))
						break;
				}
				s.close();
				handler.close();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		public CollectorWorker(MessageHandler msg_handler,
				ArrayBlockingQueue<String> nicks) {
			handler = msg_handler;
			nick_list = nicks;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			File logfolder = new File("dcdumps");
			logfolder.mkdir();
			while (true) {
				String nick = "";
				try {
					nick = nick_list.take();
					if (nick.equals("COLLECTOR_QUIT"))
						break;
					ServerSocket s_listen;
					int avl_port = 15142;
					while (true) {
						try {
							s_listen = new ServerSocket(avl_port);
						} catch (BindException e) {
							avl_port++;
							continue;
						}
						break;
					}
					s_listen.setSoTimeout(10000);// 10 Second Timeout for
													// accepting conneciton
					DCUser conn_nick = new DCUser();
					conn_nick.nick = nick;
					handler.send_connecttome(conn_nick, "10.1.34.72", avl_port);
					Socket s = s_listen.accept();
					String fname = nick + "." + s.getInetAddress().getHostAddress()
							+ "." + System.currentTimeMillis();
					fname = new File(logfolder, fname).toString();
					DCLogger.Log("Saving to : " + fname);
					download_file(s, "files.xml", fname);
					s_listen.close();
					s.close();

				} catch (InterruptedException | IOException e) {
					DCLogger.Log(e.toString() + " for nick : " + nick);
				}
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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
			DCMessage hubname = handler.getNextMessage();
			DCLogger.Log(hubname.toString());
			ArrayList<String> sup_list = new ArrayList<>();
			sup_list.add("NoGetINFO");
			sup_list.add("NoHello");
			handler.send_supports(sup_list);
			handler.send_key(DCFunctions.convert_lock_to_key(lock.lock_s
					.getBytes()));
			handler.send_validatenick(myuser.nick);
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
			DCUser dummy_user = new DCUser();
			dummy_user.nick = "asd3";
			ArrayBlockingQueue<String> nick_q = new ArrayBlockingQueue<String>(
					1024);
			FileListCollector context = new FileListCollector();
			CollectorWorker collector = context.new CollectorWorker(handler,
					nick_q);
			Thread collecter_thread = new Thread(collector);
			collecter_thread.start();
			//handler.send_revconnect(myuser, dummy_user);
			while (true) {
				DCMessage msg = handler.getNextMessage();
				if (msg.command != null && msg.command.equals("MyINFO")) {
					DCLogger.Log(msg.myinfo.toString());
					nick_q.put(msg.myinfo.nick);
				} else if (msg.command != null && msg.command.equals("HubQuit"))
					break;
				else {
					DCLogger.Log(msg.toString());
				}
			}
			nick_q.put("COLLECTOR_QUIT");
			s.close();
			handler.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
