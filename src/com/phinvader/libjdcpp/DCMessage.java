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
	String quit_s;

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
				key_s = new String(input, beg + 1, input.length - beg - 1);
			} else if (command.equals("HubName")) {
				hubname_s = new String(input, beg + 1, input.length - beg - 1);
			} else if (command.equals("Supports")) {
				String sup_s = new String(input, beg + 1, input.length - beg
						- 1);
				supports = sup_s.split(" ");
			} else if (command.equals("Hello")) {
				String hello_s = new String(input, beg + 1, input.length - beg
						- 1);
			} else if (command.equals("MyINFO")) {
				myinfo = new DCUser();
				DCFunctions.find_next(input, beg, '$');
				int offsets[] = new int[6];
				int seglength[] = new int[6];
				int loffset = DCFunctions.find_next(input, beg + 1, '$');
				// $ALL
				offsets[0] = loffset;
				loffset = DCFunctions.find_next(input, loffset, ' ');
				seglength[0] = loffset - offsets[0];
				loffset++;
				// Nick Name
				offsets[1] = loffset;
				loffset = DCFunctions.find_next(input, loffset, ' ');
				seglength[1] = loffset - offsets[1];
				loffset++;
				// Interest String
				offsets[2] = loffset;
				loffset = DCFunctions.find_next(input, loffset, '$');
				seglength[2] = loffset - offsets[2];
				loffset++;
				// Speed specifier
				loffset = DCFunctions.find_next(input, loffset, '$') + 1; // skipping blankspace
				offsets[3] = loffset;
				loffset = DCFunctions.find_next(input, loffset, '$');
				seglength[3] = loffset - offsets[3];
				loffset++;
				// email
				//$MyINFO $ALL downloadinghub awesome user123<++ V:0.75,M:A,H:1/0/0,S:5>$ $1\001$asd@asd$8270469159$
				offsets[4] = loffset;
				loffset = DCFunctions.find_next(input, loffset, '$');
				seglength[4] = loffset - offsets[4];
				loffset++;
				// Share Size
				offsets[5] = loffset;
				loffset = DCFunctions.find_next(input, loffset, '$');
				seglength[5] = loffset - offsets[5];
				if (new String(input, offsets[0], seglength[0]).equals("$ALL")) {
					myinfo.nick = new String(input, offsets[1], seglength[1]);
					myinfo.description = new String(input, offsets[2],
							seglength[2]);
					int desc_split_offset = myinfo.description.lastIndexOf('<'); // Split for the tag
					myinfo.tag = myinfo.description.substring(desc_split_offset);
					if(myinfo.tag.contains("M:A"))
						myinfo.active = true;
					else
						myinfo.active = false;
					myinfo.description = myinfo.description.substring(0, desc_split_offset);
					myinfo.connection_speed = new String(input, offsets[3],
							seglength[3] - 1);
					myinfo.speed_id = input[offsets[3] + seglength[3] - 1];
					myinfo.email = new String(input,offsets[4],seglength[4]);
					myinfo.share_size = Long.parseLong(new String(input,
							offsets[5], seglength[5]));
				} else {
					parse_success = false;
				}
			} else if (command.equals("ValidateNick")) {
			} else if (command.equals("Version")) {
			} else if (command.equals("GetNickList")) {
			} else if (command.equals("ConnectToMe")) {
			} else if (command.equals("Quit")) {
				quit_s = new String(input, beg + 1, input.length - beg
						- 1);
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
