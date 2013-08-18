package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.Socket;

public class DCRevconnect {

	public Socket s;

	private DCUser target_user;
	private DCUser my_user;
	private DCPreferences prefs;
	private String local_filename;
	private String remote_filaname;
	private DCClient downloader;
	private DCClient client;
	public static enum DownloadStatus {
		UNDEFINED, INITIATED, STARTED, DOWNLOADING, COMPLETED, INTERUPTED, FAILED, SHUTDOWN;
	}

	public long getDownloadBytes() {
		return downloader.getDownloadBytes();
	}
	public long getDownloadFileFullSize() {
		return downloader.getDownloadFileFullSize();
	}

	private DownloadStatus currentDownloadStatus = DownloadStatus.UNDEFINED;

	public DownloadStatus getCurrentDownloadStatus() {
		return currentDownloadStatus;
	}

	public void setCurrentDownloadStatus(DownloadStatus currentDownloadStatus) {
		this.currentDownloadStatus = currentDownloadStatus;
	}

	public boolean stopDownloadHandler() {
		try {
			s.close();
			this.currentDownloadStatus = DownloadStatus.SHUTDOWN;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public DCRevconnect(DCUser target_user, DCUser my_user,
			DCPreferences prefs, String local_filename, String remote_filaname,
			DCClient client) {
		super();
		this.target_user = target_user;
		this.my_user = my_user;
		this.prefs = prefs;
		this.local_filename = local_filename;
		this.remote_filaname = remote_filaname;
		this.client = client;
		DCLogger.Log(target_user.toString());
	}


	public DCUser getTarget_user() {
		return target_user;
	}
	public void setTarget_user(DCUser target_user) {
		this.target_user = target_user;
	}
	public DCUser getMy_user() {
		return my_user;
	}
	public void setMy_user(DCUser my_user) {
		this.my_user = my_user;
	}
	public String getLocal_filename() {
		return local_filename;
	}
	public void setLocal_filename(String local_filename) {
		this.local_filename = local_filename;
	}
	public String getRemote_filaname() {
		return remote_filaname;
	}
	public void setRemote_filaname(String remote_filaname) {
		this.remote_filaname = remote_filaname;
	}

}
