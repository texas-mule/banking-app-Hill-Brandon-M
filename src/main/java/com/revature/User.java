package com.revature;


public class User extends SynchronizedData<Integer> implements Accessible {

	protected enum AccessLevel {
		CLIENT, EMPLOYEE, ADMIN
	}

	private static int idAllocator = 0;

	private String username;
	private String password;
	private AccessLevel authorization;

	/**
	 * Constructs a User with the provided properties
	 * 
	 * @param id
	 *            - The id of the User
	 * @param username
	 *            - The username of the User
	 * @param password
	 *            - The password of the User
	 * @param authorization
	 *            - The authorization level of the User
	 */
	protected User (Integer id, String username, String password, AccessLevel authorization) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorization = authorization;
	}

	/**
	 * Constructs a User with empty property fields
	 */
	public User () {
		new User(-1, null, null, null);
	}

	/**
	 * Constructs a new User to be added to user base. If the data confirmations
	 * do not match, the resultant User will have all its properties set to
	 * null.
	 * 
	 * @param username
	 *            - The username of the new User.
	 * @param confirmUsername
	 *            - A string used to confirm the choice of username.
	 * @param password
	 *            - The password for the new User.
	 * @param confirmPassword
	 *            - A string used to confirm the choice of password.
	 */
	public User (String username, String confirmUsername, String password, String confirmPassword) {
		if (username != confirmUsername || password != confirmPassword) {
			new User(idAllocator++, username, password, AccessLevel.CLIENT);

			// TODO: Define the behavior that adds new users to data storage
			
		} else {
			new User();
		}
	}

	/**
	 * Retrieves User from the current user base using the login information.
	 * 
	 * If the username/password combination doesn't match, the User generated
	 * will have all of its properties set to null.
	 * 
	 * @param username
	 *            - The username of the User
	 * @param password
	 *            - The password of the User
	 */
	public User (String username, String password) {
		// TODO: Define credential authentication behavior
		new User();
	}

	// TODO: define personal data

	/**
	 * Checks a provided password with the password associated with this User.
	 * 
	 * @param guess
	 *            - The password provided to be checked against the true
	 *            	password
	 * @return Whether the guessed password matches the true password or not.
	 */
	public boolean checkPassword (String guess) { return (guess == this.password); }

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

	public Integer getId () { return id; }

	public String getUsername () { return username;	}

	public AccessLevel getAuthorization () { return this.authorization;	}

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
	protected Integer generateId () {
		return idAllocator++;
		
	}

}
