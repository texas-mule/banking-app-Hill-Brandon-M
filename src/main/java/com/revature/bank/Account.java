package com.revature.bank;

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
	
	/**
	 * Increases account balance after checking for invalid input.
	 * 
	 * @param amount - the amount to deposit
	 * @return true if the deposit was successful, and false if the 
	 * amount to deposit was invalid. (ex: amount <= 0)
	 */
	public boolean deposit (Double amount) {
		if (amount <= 0) 
			return false;
		
		this.balance += amount;
		return true;
	}
	
	/**
	 * Decreases account balance after checking for overdrawing.
	 * 
	 * @param amount - the amount to withdraw
	 * @return true if the withdrawal was successful, and false if the 
	 * amount to deposit was invalid. (ex: amount <= 0)
	 */
	public boolean withdraw (Double amount) {
		if (amount <= 0 || amount > this.getBalance()) 
			return false;
		
		this.balance -= amount;
		return true;
	}
	
	/**
	 * Moves money from this account to another after checking for invalid input.
	 * 
	 * @param amount - the amount to transfer
	 * @param dest - the destination account
	 * @return true of the transfer was successful, false if the input given is invalid.
	 * (ex: amount > this.getBalance)
	 */
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
	
	@Override
	public String toString() {
		return ("ACCOUNT:[ id:" + this.id + ", balance: $" + this.balance + ", status: " + this.status.toString() + " ]");
	}

}
