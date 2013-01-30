package com.phinvader.libjdcpp;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class DCMessageTest {

	@Test
	public void testParse_message_lock() {
		String msg_s = "$Lock EXTENDEDPROTOCOLABCABCABCABCABCABC Pk=py-dchub-0.2.4--";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("EXTENDEDPROTOCOLABCABCABCABCABCABC", msg.lock_s);
		assertEquals("py-dchub-0.2.4--", msg.key_s);
		assertEquals("Lock", msg.command);
	}

	@Test
	public void testParse_message_general() {
		String msg_s = "Lock this Pk=is a dummy message not to be command parsed";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals(null, msg.command);
		assertEquals(msg_s, msg.msg_s);
	}

	@Test
	public void testParse_message_key() {
		String msg_s = "$Key XKAXKA@E@(%)@";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("XKAXKA@E@(%)@", msg.key_s);
		assertEquals("Key", msg.command);
	}

	@Test
	public void testParse_message_hubname() {
		String msg_s = "$HubName libjdcppHub";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("libjdcppHub", msg.hubname_s);
		assertEquals("HubName", msg.command);
	}

	@Test
	public void testParse_message_supports() {
		String msg_s = "$Supports NoGetINFO NoHello UserCommand UserIP2";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("Supports", msg.command);
		String[] expected = { "NoGetINFO", "NoHello", "UserCommand", "UserIP2" };
		assertArrayEquals(expected, msg.supports);
	}

}
