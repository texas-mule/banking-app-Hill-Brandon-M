package com.revature;


public interface Accessible {
	public boolean readableBy(User user);
	public boolean writableBy(User user);
}
