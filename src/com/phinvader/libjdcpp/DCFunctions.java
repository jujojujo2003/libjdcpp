/**
 * 
 */
package com.phinvader.libjdcpp;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;

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
	public static byte[] convert_lock_to_key(byte[] lock) {
		int len = lock.length;
		byte[] key = new byte[len];
		for (int i = 1; i < len; i++)
			key[i] = (byte) (lock[i] ^ lock[i - 1]);
		key[0] = (byte) (lock[0] ^ lock[len - 1] ^ lock[len - 2] ^ 5);
		for (int i = 0; i < len; i++)
			key[i] = (byte) (((key[i] << 4) & 240) | ((key[i] >> 4) & 15));
		String key_string = new String();
		for (int i = 0; i < len; i++) {
			if (key[i] == 0)
				key_string += "/%DCN000%/";
			else if (key[i] == 5)
				key_string += "/%DCN005%/";
			else if (key[i] == 36)
				key_string += "/%DCN036%/";
			else if (key[i] == 96)
				key_string += "/%DCN096%/";
			else if (key[i] == 124)
				key_string += "/%DCN124%/";
			else if (key[i] == 126)
				key_string += "/%DCN126%/";
			else
				key_string += (char) key[i];
		}
		return toBytes(key_string);
	}

	/**
	 * This is a simple function to convert a String s to a byte array as is.
	 * This is used instead of String.getBytes() because String.getBytes() uses
	 * default Charset to encode. This is problematic in several situations
	 * where as is value is used
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] toBytes(String s) {
		byte[] ret = new byte[s.length()];
		for (int i = 0; i < s.length(); i++)
			ret[i] = (byte) s.charAt(i);
		return ret;
	}
	
	/**
	 * function used while tokenizing within a byte array
	 * @param b
	 * @param offset
	 * @param c
	 * @return
	 */
	public static int find_next(byte[] b, int offset, char c) {
		int i;
		for(i = offset; i < b.length; i++) {
			if((char)b[i] == c)
				return i;
		}
		return -1;
	}
	

}
