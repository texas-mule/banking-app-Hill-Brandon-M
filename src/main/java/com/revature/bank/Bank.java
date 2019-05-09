package com.revature.bank;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Bank {
	
	private BankDatabase db;
	
	private String name;
	
	public Bank (String name, String url, String username, String password) {
		this.name = name;
		this.db = new BankDatabase(url, username, password);
	}
	
	public String getName () { return this.name; }
	
	/**
	 * Retrieves user data corresponding to a username-password combination.
	 * @param username - the username of the desired user.
	 * @param password - the password corresponding to the desired user.
	 * @return The user found on the database, or null if none was found.
	 */
	public User authenticate (String username, String password) {
		return this.db.fetchUser(username, password);
	}
	
	/**
	 * Retrieves all account data and permissions for a provided user.
	 * @param u - The user to check for
	 * @return The access data for all accounts associated with the user.
	 */
	public ArrayList<Permissions> getAccessibleAccounts (User u) {
		ArrayList<Permissions> output = this.db.fetchPermissions(u);
		
		if (output.isEmpty())
			return new ArrayList<>();
		
		return output;
	}
	
	/**
	 * Closes the database connection.
	 */
	public void close() {
		this.db.close();
	}
	
	/**
	 * Executes a withdrawal transaction.
	 * @param u - the user initiating the transaction
	 * @param a - the account the transaction is acting on
	 * @param amount - the amount withdrawn.
	 * @return True if the transaction was successful, or false if the operation failed.
	 */
	public boolean withdraw (User u, Account a, double amount) {
		Permissions p = this.db.fetchPermissions(u, a);
		double bal = a.getBalance();
		if (a.getStatus() != Account.state.CLEAR) {
			return false;
		}
		
		if ((p.canWithdraw() && a.withdraw(amount)) || u.getAuthorization() == User.AccessLevel.ADMIN) {
			Transaction t = new Transaction(
				null, 
				u, 
				Transaction.Types.WITHDRAW, 
				amount, 
				new Timestamp(System.currentTimeMillis()), 
				a, 
				a, 
				Transaction.States.PENDING, 
				"Withdrawal"
			);
			this.db.storeTransaction(t);
			this.db.storeAccount(a);
			
			return true;
			
		} else {
			a.setBalance(bal);
			return false;
		}
	}
	
	/**
	 * Executes a deposit transaction.
	 * @param u - the user initiating the transaction
	 * @param a - the account the transaction is acting on
	 * @param amount - the amount withdrawn.
	 * @return True if the transaction was successful, or false if the operation failed.
	 */
	public boolean deposit (User u, Account a, double amount) {
		Permissions p = this.db.fetchPermissions(u, a);
		double bal = a.getBalance();
		if (a.getStatus() != Account.state.CLEAR) {
			return false;
		}
		if ((p.canDeposit() && a.deposit(amount)) || u.getAuthorization() == User.AccessLevel.ADMIN) {
			Transaction t = new Transaction(
				null, 
				u, 
				Transaction.Types.DEPOSIT, 
				amount, 
				new Timestamp(System.currentTimeMillis()), 
				a, 
				a, 
				Transaction.States.PENDING, 
				"Deposit"
			);
			this.db.storeTransaction(t);
			this.db.storeAccount(a);
			
			return true;
			
		} else {
			a.setBalance(bal);
			return false;
		}
	}
	
	/**
	 * Executes a transfer transaction from a source account into a destination account
	 * @param u - The user initiating the transaction
	 * @param src - the source account of the transaction
	 * @param dst - the destination account of the transaction
	 * @param amount - the amount to transfer
	 * @param memo - A string used to describe the transaction.
	 * @return True if the transfer was successful, or false if the transfer failed.
	 */
	public boolean transfer (User u, Account src, Account dst, double amount, String memo) {
		
		
		Permissions p_src = this.db.fetchPermissions(u, src);
		Permissions p_dst = this.db.fetchPermissions(u, dst);
		
		if (src.getStatus() != Account.state.CLEAR || dst.getStatus() != Account.state.CLEAR) {
			System.err.println("Non-approved accounts used!");
			return false;
		}
		
		if ((p_src == null || p_dst == null) && u.getAuthorization() != User.AccessLevel.ADMIN ) {
			return false;
		}
		
		double bal_src = src.getBalance();
		double bal_dst = dst.getBalance();
		if ((p_src.canWithdraw() && p_dst.canDeposit() && src.withdraw(amount) && dst.deposit(amount)) || u.getAuthorization() == User.AccessLevel.ADMIN) {
			Transaction t = new Transaction(
				null, 
				u, 
				Transaction.Types.TRANSFER, 
				amount, 
				new Timestamp(System.currentTimeMillis()), 
				src, 
				dst, 
				Transaction.States.PENDING, 
				memo
			);
			this.db.storeTransaction(t);
			this.db.storeAccount(src);
			this.db.storeAccount(dst);
			
			return true;
			
		} else {
			System.err.println("Transfer failed");
			src.setBalance(bal_src);
			dst.setBalance(bal_dst);
			return false;
		}
	}
	
	/**
	 * Retrieves user information from the database after checking that a 
	 * provided user has access to that data.
	 * 
	 * @param operator - the user requesting the information
	 * @param id - the primary key ID of the desired user data
	 * 
	 * @return The user data requested, or null if the data was not found 
	 * or if the operator doesn't have access to the information..
	 */
	public User getUserInformation(User operator, Integer id) {
		if (operator.getId() != id && operator.getAuthorization() == User.AccessLevel.CLIENT) {
			return null;
		
		} else {
			return this.db.fetchUser(id);
		}
	}
	
	/**
	 * Retrieves a list of all pending accounts after checking that the 
	 * requesting user has access to that feature.
	 * 
	 * @param operator - the user requesting the data
	 * @return The list of pending accounts.
	 */
	public ArrayList<Account> getPendingAccounts (User operator) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT) {
			return new ArrayList<>();
		} else {
			return this.db.fetchAccounts(Account.state.PENDING_APPROVAL);
		}
	}
	
	/**
	 * Creates a new account for a provided user, adding it to the database as a pending account.
	 * @param u - the user requesting the new account
	 * @return The Account created.
	 */
	public Account newAccount (User u) {
		Account output = this.db.newAccount();
		Permissions p = new Permissions (null, u, output, true, true);
		
		this.db.initPermissions(p);
		
		return output;
	}
	
	/**
	 * Approves an account.
	 * 
	 * @param operator - the user approving the account
	 * @param a - the account to be approved
	 * 
	 * @return True if the approval succeeded, or false if the 
	 * approval failed or if the operator does not have access to this feature.
	 */
	public boolean approveAccount (User operator, Account a) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT)
			return false;
		
		a.setStatus(Account.state.CLEAR);
		
		return this.db.storeAccount(a) != 0;
	}
	
	/**
	 * Denies an account.
	 * 
	 * @param operator - the user denying the account
	 * @param a - the account to be denied
	 * 
	 * @return True if the denial succeeded, or false if the 
	 * denial failed or if the operator does not have access to this feature.
	 */
	public boolean denyAccount (User operator, Account a) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT)
			return false;
		
		a.setStatus(Account.state.DISAPPROVED);
		
		return this.db.storeAccount(a) != 0;
	}
	
	/**
	 * Freezes an account.
	 * 
	 * @param operator - the user freezing the account
	 * @param a - the account to be frozen
	 * 
	 * @return True if the freeze succeeded, or false if the 
	 * freeze failed or if the operator does not have access to this feature.
	 */
	public boolean freezeAccount (User operator, Account a) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT)
			return false;
		
		a.setStatus(Account.state.FROZEN);
		
		return this.db.storeAccount(a) != 0;
	}
	
	/**
	 * Cancels an account.
	 * 
	 * @param operator - the user canceling the account
	 * @param a - the account to be canceled
	 * 
	 * @return True if the cancel succeeded, or false if the 
	 * cancel failed or if the operator does not have access to this feature.
	 */
	public boolean cancelAccount (User operator, Account a) {
		if (operator.getAuthorization() != User.AccessLevel.ADMIN)
			return false;
		
		a.setStatus(Account.state.CANCELLED);
		
		return this.db.storeAccount(a) != 0;
	}
	
	/**
	 * Retrieves an account by looking up its ID
	 * @param id - the ID of the desired account
	 * @return The account corresponding to the given ID, or null if none was found.
	 */
	public Account getAccount (Integer id) {
		return this.db.fetchAccount(id);
	}
	
	/**
	 * Checks if the given username is not being used by any existing users.
	 * @param username - the username to check for
	 * @return True if the username is not being used by any existing users, or false if it is.
	 */
	public boolean usernameAvailable (String username) {
		return this.db.usernameAvailable(username);
	}
	
	/**
	 * Stores a user into the database to update or instantiate its data
	 * @param u - The user to store
	 * @return True if the operation succeeded, or false if it failed.
	 */
	public boolean storeUser (User u) {
		return this.db.storeUser(u) != 0;
		
	}
	
}
