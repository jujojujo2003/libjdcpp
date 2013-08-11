package com.phinvader.libjdcpp;

import java.util.HashSet;

public class UsersHandler implements DCCommand {

	/**
	 * Gets the list of all NICKS registered on the current server identified by
	 * param: handler
	 * 
	 * To use UserHandler: class MyUserHandler extends UserHandler implements
	 * DCCallback
	 * 
	 * @author madhavan
	 */
	public HashSet<DCUser> nick_q = new HashSet<DCUser>();

	/**
	 * Add a user to the list-of-online-users Updated when there is a MYINFO
	 * Message from the server
	 * 
	 * @param nick
	 * @throws InterruptedException
	 */

	public void addNick(DCUser nick) {
		nick_q.add(nick);
		return;
	}

	/**
	 * Delete a user from the list-of-online-users This is updated when there is
	 * a QUIT message from the server
	 * 
	 * @param nick
	 */

	public void deleteNick(String nick) {
		DCUser user = new DCUser();
		user.nick = nick;
		nick_q.remove(user);
	}

	/**
	 * DCCommand interface override Should subscribe to MyInfo and Quit to the
	 * router.
	 * 
	 * @param msg
	 */

	@Override
	public void onCommand(DCMessage msg) {
		String command = msg.command;
		if (command.equals("Quit")) {
			String quit_command = msg.toString();
			String[] quit_arr = quit_command.split(":");
			String nick_to_delete = quit_arr[1].trim();
			deleteNick(nick_to_delete);
		} else if (command.equals("MyINFO")) {
			addNick(msg.myinfo);
		}
	}

}
