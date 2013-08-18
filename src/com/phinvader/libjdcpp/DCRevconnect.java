package com.phinvader.libjdcpp;

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
	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
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

	public DCPreferences getPrefs() {
		return prefs;
	}

	public void setPrefs(DCPreferences prefs) {
		this.prefs = prefs;
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

	public DCClient getDownloader() {
		return downloader;
	}

	public void setDownloader(DCClient downloader) {
		this.downloader = downloader;
	}

	public DCClient getClient() {
		return client;
	}

	public void setClient(DCClient client) {
		this.client = client;
	}

	public static enum DownloadStatus {
		UNDEFINED, INITIATED, STARTED, DOWNLOADING, COMPLETED, INTERUPTED, FAILED, SHUTDOWN;
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
}
