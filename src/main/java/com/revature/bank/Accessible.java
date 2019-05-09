package com.revature.bank;


public interface Accessible {
	/**
	 * Checks if the given user has read access to this user data.
	 * 
	 * @param user - the given user 
	 * 
	 * @return Whether or not the given user has read access to this user object's data. 
	 */
	public boolean readableBy(User user);
	
	/**
	 * Checks if the given user has write access to this user data.
	 * 
	 * @param user - the given user 
	 * 
	 * @return Whether or not the given user has write access to this user object's data. 
	 */
	public boolean writableBy(User user);
}
