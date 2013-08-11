package com.phinvader.libjdcpp;

import static org.junit.Assert.*;

import org.junit.Test;

public class UsersHandlerTest {
	private DCUser make_user(String nick){
		DCUser user = new DCUser();
		user.nick = nick;
		return user;
	}
	
	@Test
	public void test() {
		UsersHandler u = new UsersHandler();
		u.addNick(make_user("abcd"));
		u.addNick(make_user("efgh"));
		u.addNick(make_user("ijkl"));
		assertEquals(3, u.nick_q.size());
		u.deleteNick("abcd");
		u.deleteNick("abcd");
		assertEquals(2, u.nick_q.size());
		assertTrue(u.nick_q.contains(make_user("efgh")));
		assertTrue(u.nick_q.contains(make_user("ijkl")));
	}

}
