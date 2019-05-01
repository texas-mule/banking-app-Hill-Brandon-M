package com.revature;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Bank {
	
	private BankDatabase db;
	
	private String name;
	
	public Bank (String name, String url, String username, String password) {
		this.name = name;
		this.db = new BankDatabase(url, username, password);
	}
	
	public String getName () {return this.name; }
	
	public User authenticate (String username, String password) {
		return this.db.fetchUser(username, password);
	}
	
	public ArrayList<Permissions> getAccessibleAccounts (User u) {
		ArrayList<Permissions> output = this.db.fetchPermissions(u);
		
		if (output.isEmpty())
			return null;
		
		return output;
	}
	
	public void close() {
		this.db.close();
	}
	
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
	
	public boolean transfer (User u, Account src, Account dst, double amount, String memo) {
		
		
		Permissions p_src = this.db.fetchPermissions(u, src);
		Permissions p_dst = this.db.fetchPermissions(u, dst);
		
		if (src.getStatus() != Account.state.CLEAR || dst.getStatus() != Account.state.CLEAR) {
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
			src.setBalance(bal_src);
			dst.setBalance(bal_dst);
			return false;
		}
	}
	
	public User getUserInformation(User operator, Integer id) {
		if (operator.getId() != id && operator.getAuthorization() == User.AccessLevel.CLIENT) {
			return null;
		
		} else {
			return this.db.fetchUser(id);
		}
	}
	
	public ArrayList<Account> getPendingAccounts (User operator) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT) {
			return new ArrayList<>();
		} else {
			return this.db.fetchAccounts(Account.state.PENDING_APPROVAL);
		}
	}
	
	public Account newAccount (User u) {
		Account output = this.db.newAccount();
		Permissions p = new Permissions (null, u, output, true, true);
		
		this.db.initPermissions(p);
		
		return output;
	}
	
	public boolean approveAccount (User operator, Account a) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT)
			return false;
		
		a.setStatus(Account.state.CLEAR);
		
		return this.db.storeAccount(a) != 0;
	}
	
	public boolean denyAccount (User operator, Account a) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT)
			return false;
		
		a.setStatus(Account.state.DISAPPROVED);
		
		return this.db.storeAccount(a) != 0;
	}
	
	public boolean freezeAccount (User operator, Account a) {
		if (operator.getAuthorization() == User.AccessLevel.CLIENT)
			return false;
		
		a.setStatus(Account.state.FROZEN);
		
		return this.db.storeAccount(a) != 0;
	}
	
	public boolean cancelAccount (User operator, Account a) {
		if (operator.getAuthorization() != User.AccessLevel.ADMIN)
			return false;
		
		a.setStatus(Account.state.CANCELLED);
		
		return this.db.storeAccount(a) != 0;
	}
	
	public Account getAccount (Integer id) {
		return this.db.fetchAccount(id);
	}
	
	public boolean usernameAvailable (String username) {
		return this.db.usernameAvailable(username);
	}
	
	public boolean storeUser (User u) {
		return this.db.storeUser(u) != 0;
		
	}
	
}
