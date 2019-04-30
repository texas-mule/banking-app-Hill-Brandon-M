package com.revature;

public class Permissions {
	private Integer id;
	private User user;
	private Account account;
	
	private boolean canWithdraw;
	private boolean canDeposit;
	
	public Permissions (Integer id, User user, Account account, boolean canWithdraw, boolean canDeposit) {		
		this.id = id;
		this.user = user;
		this.account = account;
		this.canWithdraw = canWithdraw;
		this.canDeposit = canDeposit;
	}
	
	public boolean canWithdraw () {	return this.canWithdraw; }
	
	public boolean canDeposit () { return this.canDeposit; }
	
	public Integer getId () {return this.id; }
	
	public User getUser () { return this.user; }
	
	public Account getAccount () { return this.account; }
}
