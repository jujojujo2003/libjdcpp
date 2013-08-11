package com.phinvader.libjdcpp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
	public void testParse_message_hello() {
		String msg_s = "$Hello libjdcppclient";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("libjdcppclient", msg.hello_s);
		assertEquals("Hello", msg.command);
	}

	@Test
	public void testParse_message_supports() {
		String msg_s = "$Supports NoGetINFO NoHello UserCommand UserIP2";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("Supports", msg.command);
		String[] expected = { "NoGetINFO", "NoHello", "UserCommand", "UserIP2" };
		assertArrayEquals(expected, msg.supports);
	}

	@Test
	public void testParse_message_myinfo1() {
		String msg_s = "$MyINFO $ALL downloadinghub awesome user123<++ V:0.75,M:A,H:1/0/0,S:5>$ $1\001$$8270469159$";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("MyINFO", msg.command);
		assertEquals("downloadinghub", msg.myinfo.nick);
		assertEquals("awesome user123", msg.myinfo.description);
		assertEquals("<++ V:0.75,M:A,H:1/0/0,S:5>", msg.myinfo.tag);
		assertEquals(true, msg.myinfo.active);
		assertEquals("1", msg.myinfo.connection_speed);
		assertEquals(1, msg.myinfo.speed_id);
		assertEquals(8270469159l, msg.myinfo.share_size);
	}

	@Test
	public void testParse_message_myinfo2() {
		String msg_s = "$MyINFO $ALL svsc hii<++ V:0.75,M:P,H:1/0/0,S:3,O:3>$ $0.005\001$s@gmail.com$15943746752$";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("MyINFO", msg.command);
		assertEquals("svsc", msg.myinfo.nick);
		assertEquals("hii", msg.myinfo.description);
		assertEquals("<++ V:0.75,M:P,H:1/0/0,S:3,O:3>", msg.myinfo.tag);
		assertEquals(false, msg.myinfo.active);
		assertEquals("0.005", msg.myinfo.connection_speed);
		assertEquals(1, msg.myinfo.speed_id);
		assertEquals("s@gmail.com", msg.myinfo.email);
		assertEquals(15943746752l, msg.myinfo.share_size);
	}

	@Test
	public void testParse_message_quit() {
		String msg_s = "$Quit someuser123";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("Quit", msg.command);
		assertEquals("someuser123", msg.quit_s);
	}
	@Test
	public void testParse_message_connect_to_me() {
		String msg_s = "$ConnectToMe libjdcpptest 1.2.3.4:60355";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("ConnectToMe", msg.command);
		assertEquals("libjdcpptest", msg.connect_nick);
		assertEquals("1.2.3.4", msg.host_name);
		assertEquals(60355, msg.port_number);
	}
	@Test
	public void testParse_message_direction() {
		String msg_s = "$Direction Upload 29798";
		DCMessage msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("Direction", msg.command);
		assertEquals(msg.dir_download, false);
		assertEquals(msg.dir_no, 29798);
		msg_s = "$Direction Download 21128";
		msg = DCMessage.parse_message(msg_s.getBytes());
		assertEquals("Direction", msg.command);
		assertEquals(msg.dir_download, true);
		assertEquals(msg.dir_no, 21128);
		
	}
}
