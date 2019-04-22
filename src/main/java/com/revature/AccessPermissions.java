package com.revature;


public class AccessPermissions  extends SynchronizedData<Integer>{
	private static DataStore <AccessPermissions, Integer> source = null;
	
	private Account account;
	
	private boolean canWithdraw;
	private boolean canDeposit;
	
	public AccessPermissions (User user, Account account) {
		this.account = account;
		
		//TODO: define permission retrieval
		this.canWithdraw = false;
		this.canDeposit = false;
	}
	
	public boolean canWithdraw () {	return this.canWithdraw; }
	
	public boolean canDeposit () { return this.canDeposit; }
	
	public final Account getAccount () { return this.account; }

	@Override
	public boolean readableBy (User user) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean writableBy (User user) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Integer generateId () {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getId () {

		// TODO Auto-generated method stub
		return null;
	}
}
