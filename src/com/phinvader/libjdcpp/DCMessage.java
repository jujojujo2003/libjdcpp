package com.phinvader.libjdcpp;


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
	DCUser hisinfo;
	String quit_s;
	String host_name; // ConnectToMe
	int port_number; // ConnectToMe
	String connect_nick; // ConnectToMe
	boolean dir_download; // If direction is download in a $Direction message
	int dir_no;
	long file_length;
	String file_path;
	String file_signature;

	/**
	 * Parses a byte[] array to produce a DCMessage Object.
	 * 
	 * sets {@link DCMessage#command} to be the command of recieved message.
	 * Depending on the message recieved more fields are populated. If parsing
	 * message was unsuccessful it defaults to a text message and is set in
	 * msg_s , and command is set to null. List of handled messages: (so far)
	 * <ul>
	 * <li>$Lock</li>
	 * <li>$Key</li>
	 * <li>$HubName</li>
	 * <li>$Supports</li>
	 * <li>$Hello</li>
	 * <li>$MyINFO</li>
	 * <li>$Quit</li>
	 * <li>$HubQuit - this is not part of the protocol but added as a way to
	 * notify when the hub socket gets cut.</li>
	 * <li></li>
	 * <li></li>
	 * </ul>
	 * 
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
	 * @see {@link DCMessage#parse_message(byte[])}
	 */
	public void parse(byte[] input) {

		boolean parse_success = true;
		try {

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
				} else if (command.equals("SR")) {
					// Search Result
					String value = new String(input);
					String[] values = value.split(" ");
					String usernick = values[1];
					String signature = values[values.length - 2];
					String path = "";
					for (int i = 2; i < values.length - 2; i++) {
						path += values[i];
						if(i!=values.length - 3)
							path+=" ";
					}
					hisinfo = new DCUser();
					hisinfo.nick = usernick;
					file_path = path;
					file_signature = signature;

				} else if (command.equals("HubName")) {
					hubname_s = new String(input, beg + 1, input.length - beg
							- 1);
				} else if (command.equals("Supports")) {
					String sup_s = new String(input, beg + 1, input.length
							- beg - 1);
					supports = sup_s.split(" ");
				} else if (command.equals("Hello")) {
					hello_s = new String(input, beg + 1, input.length - beg - 1);
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
					loffset = DCFunctions.find_next(input, loffset, '$') + 1; // skipping
																				// blankspace
					offsets[3] = loffset;
					loffset = DCFunctions.find_next(input, loffset, '$');
					seglength[3] = loffset - offsets[3];
					loffset++;
					// email
					offsets[4] = loffset;
					loffset = DCFunctions.find_next(input, loffset, '$');
					seglength[4] = loffset - offsets[4];
					loffset++;
					// Share Size
					offsets[5] = loffset;
					loffset = DCFunctions.find_next(input, loffset, '$');
					seglength[5] = loffset - offsets[5];
					if (new String(input, offsets[0], seglength[0])
							.equals("$ALL")) {
						myinfo.nick = new String(input, offsets[1],
								seglength[1]);
						myinfo.description = new String(input, offsets[2],
								seglength[2]);
						int desc_split_offset = myinfo.description
								.lastIndexOf('<');
						// Split for the tag
						if (desc_split_offset != -1) {
							myinfo.tag = myinfo.description
									.substring(desc_split_offset);
							myinfo.description = myinfo.description.substring(
									0, desc_split_offset);
						} else {
							myinfo.tag = "";
						}
						if (myinfo.tag.contains("M:A"))
							myinfo.active = true;
						else
							myinfo.active = false;
						myinfo.connection_speed = new String(input, offsets[3],
								seglength[3] - 1);
						myinfo.speed_id = input[offsets[3] + seglength[3] - 1];
						myinfo.email = new String(input, offsets[4],
								seglength[4]);
						myinfo.share_size = Long.parseLong(new String(input,
								offsets[5], seglength[5]));
					}

					else {
						parse_success = false;
					}
					// } else if (command.equals("ValidateNick")) {
					// } else if (command.equals("Version")) {
					// } else if (command.equals("GetNickList")) {
				} else if (command.equals("FileLength")) {
					file_length = Long.parseLong(new String(input, beg + 1,
							input.length - beg - 1));
				} else if (command.equals("MyNick")) {
					hisinfo = new DCUser();
					hisinfo.nick = new String(input, beg + 1, input.length
							- beg - 1);
				} else if (command.equals("Direction")) {
					String dir_str[] = new String(input, beg + 1, input.length
							- beg - 1).split("\\s");
					dir_download = true;
					if (!dir_str[0].equals("Download"))
						dir_download = false;
					dir_no = Integer.parseInt(dir_str[1]);
				} else if (command.equals("ConnectToMe")) {
					String conn_str = new String(input, beg + 1, input.length
							- beg - 1);
					connect_nick = conn_str.split("\\s")[0];
					host_name = conn_str.split("\\s")[1];
					port_number = Integer.parseInt(host_name.split(":")[1]);
					host_name = host_name.split(":")[0];
				} else if (command.equals("HubQuit")) {
					if (input.length - beg - 1 > 0)
						quit_s = new String(input, beg + 1, input.length - beg
								- 1);
					else
						quit_s = "";
					// return just command as HubQuit
				} else if (command.equals("Quit")) {
					quit_s = new String(input, beg + 1, input.length - beg - 1);
				} else {
					parse_success = false;
				}
			} else if (input[0] == '<') {
				command = "BoardMessage";
				msg_s = new String(input);
			} else {
				parse_success = false;
			}
			if (!parse_success) {
				command = null;
				msg_s = new String(input);
			}
		} catch (Exception e) {
			// TODO Remove This debugging piece
			command = null;
			msg_s = "ParseFail : " + e.toString();
			msg_s += "\n For:" + new String(input);
		}
	}

	public String toString() {
		String desc = command;
		if (desc == null)
			return msg_s;
		desc += " : ";
		if (command.equals("Lock")) {
			desc += lock_s;
			desc += " pk : " + key_s;
		} else if (command.equals("Key")) {
			desc += key_s;
		} else if (command.equals("HubName")) {
			desc += hubname_s;
		} else if (command.equals("Supports")) {
			for (String s : supports)
				desc += s + " ";
		} else if (command.equals("Hello")) {
			desc += hello_s;
		} else if (command.equals("MyINFO")) {
			desc += myinfo.nick;
		} else if (command.equals("HubQuit")) {
			desc += quit_s;
		} else if (command.equals("Quit")) {
			desc += quit_s;
		} else if (command.equals("ConnectToMe")) {
			desc += connect_nick + " " + host_name + ":" + port_number;
		} else if (command.equals("Direction")) {
			String s = "Download";
			if (!dir_download)
				s = "Upload";
			s += " " + dir_no;
			desc += s;
		} else if (command.equals("FileLength")) {
			desc += Long.toString(file_length);
		} else if (command.equals("SR")) {
			desc += "@" + hisinfo.nick + " " + file_path + " : "
					+ file_signature;
		} else if (command.equals("BoardMessage")) {
			desc += msg_s;
		}

		return desc;
	}

}
