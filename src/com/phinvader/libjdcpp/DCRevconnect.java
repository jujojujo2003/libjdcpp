package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.phinvader.libjdcpp.UsersHandler.downloadManager;

public class DCRevconnect implements DCCommand{

	public Socket s; 
	
	@Override
	public void onCommand(DCMessage msg, MessageHandler handler) {
		DCLogger.Log("in DCRevconnect"+msg.toString());
		String target_identifier = msg.toString().split(" ")[3];
		String[] ip_port_raw = target_identifier.split(":");
		String remote_ip = ip_port_raw[0];
		String remote_port = ip_port_raw[1];
		
		
		try {
			s = new Socket(remote_ip, Integer.parseInt(remote_port));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			DCLogger.Log("ERROR - Socket creation error 1");
			e.printStackTrace();
		} catch (IOException e) {
			DCLogger.Log("ERROR - Socket creation error 2");
			e.printStackTrace();
		}
		
	}

}
