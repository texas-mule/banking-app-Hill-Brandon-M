package com.revature;

public class User implements Accessible {

	protected static enum AccessLevel {
		CLIENT, EMPLOYEE, ADMIN
	}
	
	private Integer id;
	private String username;
	private String password;
	private AccessLevel authorization;
	private String firstname;
	private String lastname;
	private String ssn;
	private String birthdate;
	private String address;
	private String phone;	

	public User (Integer id, String username, String password, AccessLevel authorization, String firstname,
			String lastname, String ssn, String birthdate, String address, String phone) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorization = authorization;
		this.firstname = firstname;
		this.lastname = lastname;
		this.ssn = ssn;
		this.birthdate = birthdate;
		this.address = address;
		this.phone = phone;
	}



	
	/**
	 * Constructs a User with empty property fields
	 */
	public User () {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	public Integer getId () { return id; }

	public String getUsername () { return username;	}
	
	public String getPassword () { return this.password; }

	/**
	 * Sets the password field to a new value. If the old password is guessed
	 * incorrectly, nothing happens.
	 * 
	 * @param password
	 *            - The new password.
	 * @param oldPassword
	 *            - The old password, used to authenticate the action.
	 * @return Whether the password was changed successfully or not.
	 */
	public boolean setPassword (String password, String oldPassword) {

		if (oldPassword == this.password) {
			this.password = password;
			return true;
		}

		return false;
	}

	public AccessLevel getAuthorization () { return this.authorization;	}
	
	public String getFirstname() { return this.firstname; }
	
	public void setFirstname (String firstname) { this.firstname = firstname; }
	
	public String getLastname() { return this.lastname; }
	
	public void setLastname (String lastname) { this.lastname = lastname; }
	
	public String getSsn() { return this.ssn; }
	
	public String getBirthdate () { return birthdate; }
	
	public String getAddress () { return address; }
	
	public void setAddress (String address) { this.address = address; }
	
	public String getPhone () { return phone; }
	
	public void setPhone (String phone) { this.phone = phone; }

	@Override
	public boolean readableBy (User user) {

		switch (user.authorization) {
			case ADMIN: // Admins can read all user info
			case EMPLOYEE: // Employees can read all user info
				return true;

			case CLIENT: // Clients can only read their own info
				return (user.id == this.id);

			default:
				return false;
		}
	}

	@Override
	public boolean writableBy (User user) {

		switch (user.authorization) {
			case ADMIN: // Admins can write to all Users
				return true;

			case EMPLOYEE: // Employees can only write to self
			case CLIENT: // Client can only write to self
				return (user.id == this.id);

			default:
				return false;
		}
	}
}
