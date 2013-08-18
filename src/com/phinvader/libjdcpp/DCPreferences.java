package com.phinvader.libjdcpp;

public class DCPreferences {

	public String nick; // DC Nick
	public long share_size; //
	public String server_ip; // IP address of the server
	public static String self_ip;

	public DCPreferences(String nick, long share_size, String server_ip) {
		super();
		this.nick = nick;
		this.share_size = share_size;
		this.server_ip = server_ip;
	}

	/**
	 * Save preferences to an encoded format for storage.
	 * 
	 * @return
	 * @see DCPreferences#load_preferences(String)
	 */
	public String save_preferences() {
		// TODO auto-generated block
		return null;
	}

	/**
	 * Load preferences from a single string encoded format
	 * 
	 * @param pref_string
	 * @see DCPreferences#save_preferences()
	 */
	public void load_preferences(String pref_string) {
		// TODO auto-generated block
	}

	public static String get_self_ip() {
		return self_ip;
	}

	// Following are the Getters and Setters for the params in this class
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public long getShare_size() {
		return share_size;
	}

	public void setShare_size(long share_size) {
		this.share_size = share_size;
	}

	public String getServer_ip() {
		return server_ip;
	}

	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}

}
