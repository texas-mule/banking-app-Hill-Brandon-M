package com.revature;

import static org.junit.Assert.*;

import org.junit.*;


public class UserTest {
	
	User u;
	
	@Before
	public void initialize () {
		u = new User(
			1, 
			"example", 
			"password", 
			User.AccessLevel.CLIENT, 
			"John", 
			"Doe", 
			"XXX-XX-XXXX", 
			"XXXX/XX/XX", 
			"Somewhere", 
			"(XXX)-XXX-XXXX"
		);
	}
	
	@Test
	public void testGetUsername () {		
		assertEquals(u.getUsername(), "example");
	}
	
	@Test
	public void testGetSetPassword () {
		String r1 = u.getPassword();
		
		assertTrue(u.setPassword("new", "password"));
		
		String r2 = u.getPassword();
		
		assertEquals(r1, "password");
		assertEquals(r2, "new");
	}

	@Test
	public void testGetId () { assertTrue(u.getId() == 1); }

	@Test
	public void testGetAuthorization () {

		assertTrue(u.getAuthorization() == User.AccessLevel.CLIENT);
	}
	
	@Test
	public void testGetSetAuthourization () {
		fail("Not implemented");
	}
}
