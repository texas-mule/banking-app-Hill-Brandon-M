package com.revature;

import java.sql.*;
import java.util.ArrayList;

public class BankDatabase {
	private Connection conn = null;
	private Savepoint s;
	
	private static final String SQL_STORE_USER = 
			"INSERT INTO Users "
		+ 	"(u_username,u_password,u_auth,u_firstname,u_lastname,u_ssn,u_birthdate,u_address,u_phone)"
		+ 	"VALUES (?,?,?,?,?,?,?,?,?) "
		+ 	"ON CONFLICT (u_username) DO UPDATE SET "
		+ 	"u_password 		= EXCLUDED.u_password, "
		+ 	"u_auth 			= EXCLUDED.u_auth, "
		+ 	"u_firstname 		= EXCLUDED.u_firstname, "
		+ 	"u_lastname 		= EXCLUDED.u_lastname, "
		+ 	"u_ssn 				= EXCLUDED.u_ssn, "
		+ 	"u_birthdate 		= EXCLUDED.u_birthdate, "
		+ 	"u_address 			= EXCLUDED.u_address, "
		+ 	"u_phone 			= EXCLUDED.u_phone "
		+ 	"WHERE 	u_username 	= EXCLUDED.u_username"
		+ 	"RETURNING u_id;";
	
	private static final String SQL_RETRIEVE_USER_ID = 
			"SELECT * FROM Users WHERE u_id = ?";
	
	private static final String SQL_RETRIEVE_USER_CRED = 
			"SELECT * FROM Users WHERE u_username = ? AND u_password = ?;";
	
	private static final String SQL_UPDATE_ACCOUNT = 
			"UPDATE Accounts SET"
		+ 	"acct_balance = ?, acct_status = ? "
		+ 	"WHERE acct_id = ?;";
	
	private static final String SQL_INIT_ACCOUNT = 
			"INSERT INTO Accounts (acct_balance,acct_status) "
		+ 	"VALUES (?,?) "
		+ 	"RETURNING acct_id;";
	
	private static final String SQL_RETRIEVE_ACCOUNT_ID = 
			"SELECT * FROM Accounts WHERE acct_id = ?";
	
	private static final String SQL_RETRIEVE_PERMISSIONS_USER = 
			"SELECT * FROM Permissions "
		+ 	"JOIN Accounts ON p_acct_id = acct_id "
		+ 	"WHERE p_u_id = ?;";
	
	private static final String SQL_RETRIEVE_PERMISSIONS_ACCOUNT = 
			"SELECT * FROM Permissions "
		+ 	"JOIN Users ON p_u_id = u_id "
		+ 	"WHERE p_acct_id = ?;";
	
	private static final String SQL_RETRIEVE_PERMISSIONS_USER_ACCOUNT = 
			"SELECT * FROM Permissions "
		+ 	"WHERE p_u_id = ? AND p_acct_id = ?";
	
	//Prepared Statements
	private PreparedStatement STATEMENT_STORE_USER;
	private PreparedStatement STATEMENT_RETRIEVE_USER_ID;
	private PreparedStatement STATEMENT_RETRIEVE_USER_CRED;
	
	private PreparedStatement STATEMENT_UPDATE_ACCOUNT;
	private PreparedStatement STATEMENT_INIT_ACCOUNT;
	private PreparedStatement STATEMENT_RETRIEVE_ACCOUNT_ID;
	
	private PreparedStatement STATEMENT_RETRIEVE_PERMISSIONS_USER;
	private PreparedStatement STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT;
	private PreparedStatement STATEMENT_RETRIEVE_PERMISSIONS_USER_ACCOUNT;
	
	public BankDatabase (Connection conn) {
		this.conn = conn;		
		this.open();
	}
	
	public BankDatabase (String url, String username, String password) {
		try {
			this.conn = DriverManager.getConnection(url, username, password);
			this.s = this.conn.setSavepoint();
			this.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void open () {
		try {
			this.s = conn.setSavepoint();
			
			this.STATEMENT_STORE_USER = conn.prepareStatement(SQL_STORE_USER);
			this.STATEMENT_RETRIEVE_USER_ID = conn.prepareStatement(SQL_RETRIEVE_USER_ID);
			this.STATEMENT_RETRIEVE_USER_CRED = conn.prepareStatement(SQL_RETRIEVE_USER_CRED);
			
			this.STATEMENT_UPDATE_ACCOUNT = conn.prepareStatement(SQL_UPDATE_ACCOUNT);
			this.STATEMENT_INIT_ACCOUNT = conn.prepareStatement(SQL_INIT_ACCOUNT);
			this.STATEMENT_RETRIEVE_ACCOUNT_ID = conn.prepareStatement(SQL_RETRIEVE_ACCOUNT_ID);
			
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER = conn.prepareStatement(SQL_RETRIEVE_PERMISSIONS_USER);
			this.STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT = conn.prepareStatement(SQL_RETRIEVE_PERMISSIONS_ACCOUNT);
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER_ACCOUNT = conn.prepareStatement(SQL_RETRIEVE_PERMISSIONS_USER_ACCOUNT);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close () {
		try {
			if (this.conn.isClosed())
				return;
			
			this.conn.commit();
			this.conn.close();
			
		} catch (SQLException e) {
			System.err.println("Something went wrong when closing the database:");
			e.printStackTrace();
		}
	}
	
	public Integer storeUser (User obj) {
		try {
			this.s = conn.setSavepoint();
			
			this.STATEMENT_STORE_USER.setString	(1, obj.getUsername());
			this.STATEMENT_STORE_USER.setString	(2, obj.getPassword());
			this.STATEMENT_STORE_USER.setString	(3, obj.getAuthorization().toString());
			this.STATEMENT_STORE_USER.setString	(4, obj.getFirstname());
			this.STATEMENT_STORE_USER.setString	(5, obj.getLastname());
			this.STATEMENT_STORE_USER.setString	(6, obj.getSsn());
			this.STATEMENT_STORE_USER.setString	(7, obj.getBirthdate());
			this.STATEMENT_STORE_USER.setString	(8, obj.getAddress());
			this.STATEMENT_STORE_USER.setString	(9, obj.getPhone());
			
			Integer id = this.STATEMENT_STORE_USER.executeQuery().getInt(0);
			
			if(id == 0) {
				System.err.println("User storage failed. Rolling back...");
				this.conn.rollback(s);
			} else {
				this.conn.commit();
				this.conn.releaseSavepoint(s);
				return id;
			}
			
		} catch (SQLException e) {
			System.err.println(
					"Something went wrong when storing User " 
				+ 	obj.getUsername() + "."
			);
			e.printStackTrace();
		}			
		return 0;
	}
	
	public User fetchUser (Integer id) {
		ResultSet rs;			
		try {
			this.STATEMENT_RETRIEVE_USER_ID.setInt(1, id);
			rs = this.STATEMENT_RETRIEVE_USER_ID.executeQuery();		
		} catch (SQLException e) {
			System.err.println("Something went wrong when retrieving user with id " + id);
			e.printStackTrace();
			rs = null;
		}
		
		return parseUsers(rs).get(0);
	}
	
	public User fetchUser (String username, String password) {
		ResultSet rs;
		try {
			this.STATEMENT_RETRIEVE_USER_CRED.setString(1, username);
			this.STATEMENT_RETRIEVE_USER_CRED.setString(2, password);
			rs = this.STATEMENT_RETRIEVE_USER_CRED.executeQuery();
		} catch (SQLException e) {
			System.err.println("Something went wrong when retrieving user with username " + username);
			e.printStackTrace();
			rs = null;
		}
		
		return parseUsers(rs).get(0);
	}
	
	private static ArrayList<User> parseUsers (ResultSet rs) {
		ArrayList<User> output = new ArrayList<>();
		try {
			if ((rs == null) || (!rs.first()))
				return new ArrayList<>();
			
			while(!rs.isAfterLast()) {
				output.add(
					new User(
						rs.getInt("u_id"),
						rs.getString("u_username"),
						rs.getString("u_password"),
						User.AccessLevel.valueOf(rs.getString("u_auth")),
						rs.getString("u_firstname"),
						rs.getString("u_lastname"),
						rs.getString("u_ssn"),
						rs.getString("u_birthdate"),
						rs.getString("address"),
						rs.getString("u_phone")
					)
				);
				rs.next();
			}
			
			return output;
			
		} catch (SQLException e) {
			System.err.println("Something wen wrong when parsing retrieval results " + rs.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Account fetchAccount (Integer id) {
		ResultSet rs;			
		try {
			this.STATEMENT_RETRIEVE_ACCOUNT_ID.setInt(1, id);
			rs = this.STATEMENT_RETRIEVE_ACCOUNT_ID.executeQuery();				
		} catch (SQLException e) {
			System.err.println("Something went wrong when retrieving user with id " + id);
			e.printStackTrace();
			rs = null;
		}
		
		return parseAccounts(rs).get(0);
	}
	
	public Integer storeAccount (Account obj) {			
		if (obj == null) return 0;
		
		if (obj.getId() == null) {
			obj = this.newAccount();
			return obj.getId();
		}
		
		try {
			this.s = conn.setSavepoint();
			
			this.STATEMENT_UPDATE_ACCOUNT.setDouble(1, obj.getBalance());
			this.STATEMENT_UPDATE_ACCOUNT.setString(2, obj.getStatus().toString());
			this.STATEMENT_UPDATE_ACCOUNT.setInt(3, obj.getId());
			
			int result = this.STATEMENT_UPDATE_ACCOUNT.executeUpdate();
			
			if (result != 1) {
				conn.rollback(s);
				conn.releaseSavepoint(s);
				return 0;
			} else {
				conn.commit();
				conn.releaseSavepoint(s);
				return obj.getId();
			}
		} catch (SQLException e) {
			System.err.println("Something went wrong when storing Account " + obj.getId());
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public Account newAccount () {
		try {
			this.STATEMENT_INIT_ACCOUNT.setDouble(1, 0.00);
			this.STATEMENT_INIT_ACCOUNT.setString(2, Account.state.PENDING_APPROVAL.toString());
			
			return new Account(
				this.STATEMENT_INIT_ACCOUNT.executeQuery().getInt("acct_id"),
				0.00,
				Account.state.PENDING_APPROVAL
			);
		} catch (SQLException e) {
			e.printStackTrace();			
		}
		return null;
	}
	
	private static ArrayList<Account> parseAccounts (ResultSet rs) {
		
		ArrayList<Account> output = new ArrayList<>(); 
			
		try {
			if ((rs== null) || (!rs.first()))
				return new ArrayList<>();
			
			while (!rs.isAfterLast()) {
				output.add(
					new Account (
						rs.getInt("acct_id"),
						rs.getDouble("acct_balance"),
						Account.state.valueOf(rs.getString("acct_status"))
					)
				);
				
				rs.next();
			}
			
			return output;			
			
		} catch (SQLException e) {
			System.err.println("Something wen wrong when parsing retrieval results " + rs.toString());
			e.printStackTrace();
		}	
		
		
		return null;
	}
	
	public ArrayList<Permissions> fetchPermissions (User u) {
		
		ArrayList<Permissions> output = new ArrayList<>();
		try {
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER.setInt(1, u.getId());
			ResultSet rs = this.STATEMENT_RETRIEVE_PERMISSIONS_USER.executeQuery();
			ArrayList<Account> accounts = parseAccounts(rs);
			
			rs.first();		
			for (Account a : accounts) {
				
				output.add(
					new Permissions(
						rs.getInt("p_id"),
						u,
						a,
						rs.getBoolean("p_withdraw"),
						rs.getBoolean("p_deposit")
					)
				);
				
				if (!rs.next()) 
					break;
			}
			
			return output;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public ArrayList<Permissions> fetchPermissions (Account a) {
		ArrayList<Permissions> output = new ArrayList<>();
		try {
			this.STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT.setInt(1, a.getId());
			ResultSet rs = this.STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT.executeQuery();
			ArrayList<User> users = parseUsers(rs);
			
			rs.first();		
			for (User u : users) {
				
				output.add(
					new Permissions(
						rs.getInt("p_id"),
						u,
						a,
						rs.getBoolean("p_withdraw"),
						rs.getBoolean("p_deposit")
					)
				);
				
				if (!rs.next()) 
					break;
			}
			
			return output;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
	
	public Permissions fetchPermissions (User u, Account a) {
		
		Permissions output;
		
		try {
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER_ACCOUNT.setInt(1, u.getId());
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER_ACCOUNT.setInt(2, a.getId());
			
			ResultSet rs = this.STATEMENT_RETRIEVE_PERMISSIONS_USER_ACCOUNT.executeQuery();
			
			output = new Permissions(
				rs.getInt("p_id"),
				u,
				a,
				rs.getBoolean("p_withdraw"),
				rs.getBoolean("p_deposit")
			);
		} catch (SQLException e) {
			e.printStackTrace();
			output = null;
		}
		
		return output;
	}
	
	private ArrayList<Permissions> parsePermissions (ResultSet rs) {
		//TODO: implement permissions parsing
		return null;
	}
}
