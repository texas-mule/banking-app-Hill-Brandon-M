package com.revature.bank;

import static org.junit.Assert.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.*;


public class UserTest {
	
	User u;
	
	@Before
	public void initialize () throws ParseException {
		u = new User(
			1, 
			"example", 
			"password", 
			User.AccessLevel.CLIENT, 
			"John", 
			"Doe", 
			"XXX-XX-XXXX", 
			new Date(new SimpleDateFormat("MM-dd-yyyy").parse("11-11-1111").getTime()),
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
