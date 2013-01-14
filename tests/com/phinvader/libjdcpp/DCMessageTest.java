package com.phinvader.libjdcpp;

import static org.junit.Assert.*;

import org.junit.Test;

public class DCMessageTest {

	@Test
	public void testParse_message_lock() {
		String msg_s = "$Lock EXTENDEDPROTOCOLABCABCABCABCABCABC Pk=py-dchub-0.2.4--";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("EXTENDEDPROTOCOLABCABCABCABCABCABC",msg.lock_s);
		assertEquals("py-dchub-0.2.4--",msg.key_s);
	}
	@Test
	public void testParse_message_general() {
		String msg_s = "Lock this Pk=is a dummy message not to be command parsed";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals(msg_s,msg.msg_s);
	}

}
