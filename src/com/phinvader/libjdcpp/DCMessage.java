package com.phinvader.libjdcpp;

import java.util.ArrayList;

/**
 * This class is a universal container for all DC messages which are transfered
 * over TCP. This includes the data of a file download
 * 
 * @author phinfinity
 * 
 */
public class DCMessage {
	String msg_s; // When there is no command and it is just a text message
	String command;
	String lock_s; // Lock string in $Lock Command
	String key_s; // Key string in $Lock command or the key from $Key
	String hubname_s; // HubName given by $HubName command
	String[] supports; // List of features supported (by $Supports command
	String hello_s;
	DCUser myinfo;
	/**
	 * Parses a byte[] array to produce a DCMessage Object.
	 * 
	 * @param input
	 * @return
	 * @throws DCMessageParseException
	 */
	public static DCMessage parse_message(byte[] input) {
		DCMessage ret = new DCMessage();
		ret.parse(input);
		return ret;
	}

	/**
	 * Parses a byte[] array to populate DCMessage
	 * 
	 * @param input
	 * @throws DCMessageParseExceptionus
	 */
	public void parse(byte[] input) {

		boolean parse_success = true;
		if (input[0] == '$') {
			int beg = DCFunctions.find_next(input, 1, ' ');
			if (beg == -1)
				beg = input.length;
			command = new String(input, 1, beg - 1);
			if (command.equals("Lock")) {
				int poffset = beg + 1, coffset;
				coffset = DCFunctions.find_next(input, poffset, ' ');
				lock_s = new String(input, poffset, coffset - poffset);
				poffset = coffset + 1;
				coffset = DCFunctions.find_next(input, poffset, ' ');
				String[] key_split = new String(input, poffset, coffset
						- poffset).split("=");
				if (key_split.length != 2
						|| !key_split[0].toLowerCase().equals("pk"))
					parse_success = false;
				else
					key_s = key_split[1];

			} else if (command.equals("Key")) {
				key_s = new String(input,beg+1,input.length-beg-1);
			} else if (command.equals("HubName")) {
				hubname_s = new String(input,beg+1,input.length-beg-1);
			} else if (command.equals("Supports")) {
				String sup_s = new String(input,beg+1,input.length-beg-1);
				supports = sup_s.split(" ");
			} else if (command.equals("Hello")) {
				String hello_s = new String(input,beg+1,input.length-beg-1);
			} else if (command.equals("MyINFO")) {
				
			} else if (command.equals("ValidateNick")) {
			} else if (command.equals("Version")) {
			} else if (command.equals("GetNickList")) {
			} else if (command.equals("ConnectToMe")) {
			} else if (command.equals("Quit")) {
			} else {
				parse_success = false;
			}
		} else {
			parse_success = false;
		}
		if (!parse_success) {
			command = null;
			msg_s = new String(input);
		}

	}

}
