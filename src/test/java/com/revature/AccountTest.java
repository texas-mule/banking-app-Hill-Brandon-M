package com.revature;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class AccountTest extends Account {
	
	Account a;
	
	@Before
	public void setUp () throws Exception {
		a = new Account(0, 10.0, Account.state.CLEAR);
	}

	@Test
	public void testGetId () { assertTrue(a.getId() == 0); }

	@Test
	public void testGetBalance () { assertTrue(a.getBalance() == 10.0); }

	@Test
	public void testGetStatus () { assertTrue(a.getStatus() == Account.state.CLEAR); }

	@Test
	public void testSetStatus () {
		assertTrue(a.getStatus() == Account.state.CLEAR);
		a.setStatus(Account.state.FROZEN);
		assertTrue(a.getStatus() == Account.state.FROZEN);
	}

	@Test
	public void testDeposit () {
		assertTrue(a.deposit(10.0));
		assertTrue(a.getBalance() == 20.0);
	}

	@Test
	public void testWithdraw () {
		// Test if deposit works and new balance reflects transaction
		assertTrue(a.withdraw(10.0));
		assertTrue(a.getBalance() == 0.0);
		
		// Test if account can be overdrawn.
		assertFalse(a.withdraw(10.0));
	}

	@Test
	public void testTransfer () {

		Account b = new Account(1, 30.00, Account.state.CLEAR);
		
		assertTrue(b.transfer(10.00, a));		
		assertTrue(a.getBalance() == b.getBalance());
	}

}
