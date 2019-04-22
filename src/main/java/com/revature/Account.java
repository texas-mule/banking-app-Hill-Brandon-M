package com.revature;


public class Account extends SynchronizedData<Integer> implements Accessible {
	
	
	
	/**
	 * Defines the state of the Account
	 * <table>
	 * 	<tr>
	 * 		<td>State</td>
	 * 		<td> - Definition</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>DISAPPROVED</td>
	 * 		<td> - Account request has been denied.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>PENDING_APPROVAL</td>
	 * 		<td> - Account request is currently undergoing the approval process.</td>
	 * 	</tr>	 * 
	 * 	<tr>
	 * 		<td>CLEAR</td>
	 * 		<td> - Account has no ongoing statuses.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>FROZEN</td>
	 * 		<td> - Account is currently frozen, and no new 
	 * 			transactions may be made at this time.
	 * 		</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>UNSYNCHRONIZED</td>
	 * 		<td> - Account data is currently not synchronized with the data stored.</td>
	 * 	</tr>
	 *
	 */
	private static enum state {		
		DISAPPROVED,
		PENDING_APPROVAL,
		CLEAR,
		FROZEN,
		UNSYNCHRONIZED
	}	
	
	/**
	 * The current balance of this account.
	 */
	private double balance;
	
	/**
	 * The current status of this account.
	 */
	private state status;
	
	private Account (Integer id, double balance, state status) {
		this.id = id;
		this.balance = balance;
		this.status = status;
	}

	public Account () {
		new Account(generateId(), 0.00, Account.state.PENDING_APPROVAL);
	}
	
	public Account (int id, User user) {
		
		if (this.readableBy(user)) {
			// TODO: Define data access behavior
		}
		
	}
	
	public Integer getId () { return id; }

	
	public double getBalance () { return balance; }

	
	public state getStatus () {	return status; }

	
	public void setStatus (state status) {	this.status = status; }
	
	public boolean deposit (double amount) {
		//TODO: Implement Deposit
		return false;
	}
	
	public boolean withdraw (double amount) {
		//TODO: Implement withdrawal
		return false;
	}
	
	public boolean transfer (double amount, Account dest) {
		//TODO: Implement Transfer
		return false;
	}
	
	@Override
	public boolean readableBy (User user) {

		switch (user.getAuthorization()) {
			case ADMIN: 		// Admins can read all user info
			case EMPLOYEE: 		// Employees can read all user info
				return true;
			
			case CLIENT: 		// Clients can only read their own info
				AccessPermissions ap = this.access(user);
				return (ap.canDeposit() || ap.canWithdraw());
				
			default:
				return false;
		}
	}

	@Override
	public boolean writableBy (User user) {

		switch (user.getAuthorization()) {
			case ADMIN: 		// Admins can write to all Users
				return true;
			
			case EMPLOYEE: 		// Employees can only write to self
			case CLIENT: 		// Client can only write to self
				AccessPermissions ap = this.access(user);
				return (ap.canDeposit() && ap.canWithdraw());
			
			default:
				return false;
		}
	}
	
	public AccessPermissions access (User user) { return new AccessPermissions(user, this);	}
	
	private static Integer idAllocator = 0;
	
	@Override
	protected Integer generateId () { return idAllocator++; }	
}
