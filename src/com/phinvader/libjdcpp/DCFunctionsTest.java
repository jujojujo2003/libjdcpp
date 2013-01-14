package com.phinvader.libjdcpp;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class DCFunctionsTest {

	@Test
	public void testConvert_lock_to_key() {
		String lock = "EXTENDEDPROTOCOLABCABCABCABCABCABC";
		String actual_key_hex = "14d1c011b0a010104120d1b1b1c0c030d03010203010203010203010203010203010";
		char[] actual_key_char = new char[actual_key_hex.length()/2];
		for(int i = 0; i < actual_key_char.length; i++) {
			actual_key_char[i] = (char) (Character.digit(actual_key_hex.charAt(2*i),16)*16
					+Character.digit(actual_key_hex.charAt(2*i+1),16));
		}
		String actual_key = new String(actual_key_char);
		byte[] key = DCFunctions.convert_lock_to_key(lock.getBytes(StandardCharsets.ISO_8859_1));
		byte[] o = key;
		byte[] a = DCFunctions.toBytes(actual_key);
		assertArrayEquals(a,o);
	}

}
