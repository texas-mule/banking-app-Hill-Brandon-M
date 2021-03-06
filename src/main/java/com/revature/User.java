package com.revature;

import java.sql.Date;

public class User implements Accessible {
	
	/**
	 * Indicates the level of authorization given to this user. Can be one of the following values:
	 * <table>
	 * 	<tr>
	 * 		<td>UNIDENTIFIED</td>
	 * 		<td>
	 * 			User is not identified and should not be permitted to access any application 
	 * 			functions other than authentication.
	 * 		</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>CLIENT</td>
	 * 		<td>User is a normal client and can access the customer-focused features of the application only.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>EMPLOYEE</td>
	 * 		<td>
	 * 			User is an employee of the bank, and should have access to customer-focused 
	 * 			features and lower-level management features.
	 * 		</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>ADMIN</td>
	 * 		<td>User is a system administrator, and should have access to all features of the application.</td>
	 * 	</tr>
	 * </table>
	 *
	 */
	protected static enum AccessLevel {
		UNIDENTIFIED, CLIENT, EMPLOYEE, ADMIN
	}
	
	private Integer id;
	private String username;
	private String password;
	private AccessLevel authorization;
	private String firstname;
	private String lastname;
	private String ssn;
	private Date birthdate;
	private String address;
	private String phone;	

	public User (Integer id, String username, String password, AccessLevel authorization, String firstname,
			String lastname, String ssn, Date birthdate, String address, String phone) {
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
	
	public Date getBirthdate () { return birthdate; }
	
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
	
	@Override
	public String toString () {
		return ("USER:[ id:" + this.id + ", username:" + this.username + ", password:" + this.password + ", name:" + this.firstname + " " + this.lastname + "]");
	}
}
