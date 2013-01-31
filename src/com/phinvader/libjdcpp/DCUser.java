package com.phinvader.libjdcpp;

/**
 * The only compulsory field here is nick. Rest all may be null.
 * Mostly decided by the $MyInfo command as specified at:
 * http://wiki.gusari.org/index.php?title=$MyINFO
 * @author phinfinity
 * 
 */
public class DCUser {
	public String nick; // filled by myinfo
	public String ip;
	public long share_size; // filled by myinfo
	public String description; // filled by myinfo
	public String tag;	// filled by myinfo
	public String email; //filled by myinfo
	public String connection_speed; // filled by myinfo
	public byte speed_id; // filled by myinfo
	public boolean active; // filled by tag from myfino
	public boolean op;
	public DCUser(DCUser original) {
		nick = original.nick;
		ip = original.ip;
		share_size = original.share_size;
		description = original.description;
		tag = original.tag;
		email = original.email;
		connection_speed = original.connection_speed;
		speed_id = original.speed_id;
		active = original.active;
		op = original.op;
	}
	public DCUser() {
	}
}
