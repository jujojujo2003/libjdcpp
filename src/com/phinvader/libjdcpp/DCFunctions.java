/**
 * 
 */
package com.phinvader.libjdcpp;

/**
 * Contains several useful functions , which are required for DC connection
 * operations, such as converting between locks and keys, hashes etc.
 * 
 * @author phinfinity
 * 
 */
public class DCFunctions {

	/**
	 * Converts a Lock string in to its equivalent key string
	 * 
	 * @param lock
	 * @return
	 */
	public static String convert_lock_to_key(String lock_string) {
		byte[] lock = lock_string.getBytes();
		int len = lock.length;
		byte[] key = new byte[len];
		for (int i = 1; i < len; i++)
			key[i] = (byte) (lock[i] ^ lock[i - 1]);
		key[0] = (byte) (lock[0] ^ lock[len - 1] ^ lock[len - 2] ^ 5);
		for (int i = 0; i < len; i++)
			key[i] = (byte) (((key[i] << 4) & 240) | ((key[i] >> 4) & 15));
		String key_string = "";
		for (int i = 0; i < len; i++) {
			if (key[i] == 0)
				key_string += "/%DCN000%/";
			else if(key[i] == 5)
				key_string += "/%DCN005%/";
			else if(key[i] == 36)
				key_string += "/%DCN036%/";
			else if(key[i] == 96)
				key_string += "/%DCN096%/";
			else if(key[i] == 124)
				key_string += "/%DCN124%/";
			else if(key[i] == 126)
				key_string += "/%DCN126%/";
			else
				key_string += key[i];
		}
		return key_string;
	}

}
