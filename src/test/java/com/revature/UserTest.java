package com.revature;

import static org.junit.Assert.*;

import org.junit.*;


public class UserTest {
	
	User u;
	
	@Before
	public void initialize () {
		u = new User(1, "example", "password", User.AccessLevel.CLIENT);
	}
	
	@Test
	public void testGetUsername () {
		assertEquals(u.getUsername(), "example");
	}
	
	@Test
	public void testCheckSetPassword () {
		boolean r1 = u.checkPassword("password");
		
		u.setPassword("new", "password");
		
		boolean r2 = u.checkPassword("new");
		
		assertTrue(r1 && r2);
	}

	@Test
	public void testGetId () { assertTrue(u.getId() == 1); }

	@Test
	public void testGetAuthorization () {

		assertTrue(u.getAuthorization() == User.AccessLevel.CLIENT);
	}

}
