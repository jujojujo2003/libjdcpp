package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.phinvader.libjdcpp.UsersHandler.downloadManager;

public class DCRevconnect implements DCCommand{

	public Socket s; 

	private DCUser target_user;
	private DCUser my_user;
	private DCPreferences prefs;
	private String local_filename;
	private String remote_filaname;
	public static enum DownloadStatus{
		UNDEFINED, INITIATED, STARTED,DOWNLOADING,COMPLETED, INTERUPTED, FAILED;
	}
	
	
	
	private DownloadStatus currentDownloadStatus = DownloadStatus.UNDEFINED; 
	
	
	
	public DownloadStatus getCurrentDownloadStatus() {
		return currentDownloadStatus;
	}



	public void setCurrentDownloadStatus(DownloadStatus currentDownloadStatus) {
		this.currentDownloadStatus = currentDownloadStatus;
	}



	public DCRevconnect(DCUser target_user, DCUser my_user,
			DCPreferences prefs, String local_filename, String remote_filaname) {
		super();
		this.target_user = target_user;
		this.my_user = my_user;
		this.prefs = prefs;
		this.local_filename = local_filename;
		this.remote_filaname = remote_filaname;
	}
	
	

	@Override
	public void onCommand(DCMessage msg) {
		DCLogger.Log("in DCRevconnect"+msg.toString());
		currentDownloadStatus =  DownloadStatus.INITIATED;
		String target_identifier = msg.toString().split(" ")[3];
		String[] ip_port_raw = target_identifier.split(":");
		String remote_ip = ip_port_raw[0];
		String remote_port = ip_port_raw[1];
		
		
		try {
			s = new Socket(remote_ip, Integer.parseInt(remote_port));
			currentDownloadStatus = DownloadStatus.STARTED;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			currentDownloadStatus = DownloadStatus.FAILED;
			DCLogger.Log("ERROR - Socket creation error 1");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			DCLogger.Log("ERROR - Socket creation error 2");
			currentDownloadStatus = DownloadStatus.FAILED;
			e.printStackTrace();
			return;
		}
		currentDownloadStatus = DownloadStatus.DOWNLOADING;
		DCClient downloader = new DCClient();
		downloader.startDownloadingFile(this, my_user, local_filename, remote_filaname);
		

		
	}

}
