package com.revature;

public class Account {
	
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
	protected static enum state {		
		DISAPPROVED,
		PENDING_APPROVAL,
		CLEAR,
		FROZEN,
		CANCELLED
	}	
	
	/**
	 * The current balance of this account.
	 */
	private Double balance;
	
	/**
	 * The current status of this account.
	 */
	private state status;

	private Integer id;
	
	public Account (Integer id, Double balance, state status) {
		this.id = id;
		this.balance = balance;
		this.status = status;
	}

	public Account () {
		this(null, null, null);
	}
	
	public Account (Integer id) {
		
	}
	
	public Integer getId () { return id; }

	
	public Double getBalance () { return balance; }

	
	public state getStatus () {	return status; }

	
	public void setStatus (state status) {	this.status = status; }
	
	public boolean deposit (Double amount) {
		if (amount <= 0) 
			return false;
		
		this.balance += amount;
		return true;
	}
	
	public boolean withdraw (Double amount) {
		if (amount <= 0 || amount > this.getBalance()) 
			return false;
		
		this.balance -= amount;
		return true;
	}
	
	public boolean transfer (Double amount, Account dest) {
		if (amount <= 0 || amount > this.getBalance()) 
			return false;
		
		this.balance -= amount;
		dest.balance += amount;
		return true;
	}

	public void setBalance (double balance) {

		this.balance = balance;
		
	}
	
	public String toString() {
		return ("[ id:" + this.id + ", balance: $" + this.balance + ", status: " + this.status.toString() + " ]");
	}

}
