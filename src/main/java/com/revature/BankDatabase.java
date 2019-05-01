package com.revature;

import java.sql.*;
import java.util.ArrayList;

public class BankDatabase {
	private Connection conn = null;
	
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
		+ 	"RETURNING u_id;";
	
	private static final String SQL_RETRIEVE_USER_ID = 
			"SELECT * FROM Users WHERE u_id = ?";
	
	private static final String SQL_RETRIEVE_USER_CRED = 
			"SELECT * FROM Users WHERE u_username = ? AND u_password = ?;";
	
	private static final String SQL_UPDATE_ACCOUNT = 
			"UPDATE Accounts SET "
		+ 	"acct_balance = ?, acct_status = ? "
		+ 	"WHERE acct_id = ?;";
	
	private static final String SQL_INIT_ACCOUNT = 
			"INSERT INTO Accounts (acct_balance,acct_status) "
		+ 	"VALUES (?,?) "
		+ 	"RETURNING acct_id;";
	
	private static final String SQL_RETRIEVE_ACCOUNT_ID = 
			"SELECT * FROM Accounts WHERE acct_id = ?";
	
	private static final String SQL_RETRIEVE_ACCOUNT_STATUS = 
			"SELECT * FROM Accounts WHERE acct_status = ?";
	
	private static final String SQL_RETRIEVE_PERMISSIONS_USER = 
			"SELECT * FROM Permissions "
		+ 	"JOIN Accounts ON p_acct_id = acct_id "
		+ 	"JOIN Users ON p_u_id = u_id "
		+ 	"WHERE p_u_id = ?;";
	
	private static final String SQL_RETRIEVE_PERMISSIONS_ACCOUNT = 
			"SELECT * FROM Permissions "
		+ 	"JOIN Users ON p_u_id = u_id "
		+ 	"JOIN Accounts ON p_acct_id = acct_id "
		+ 	"WHERE p_acct_id = ?;";
	
	private static final String SQL_RETRIEVE_PERMISSIONS_USER_ACCOUNT = 
			"SELECT * FROM Permissions "
		+ 	"WHERE p_u_id = ? AND p_acct_id = ?";
	
	private static final String SQL_STORE_PERMISSIONS = 
			"UPDATE Permissions SET "
		+ 	"p_withdraw = ? "
		+ 	"p_deposit = ? "
		+ 	"WHERE p_id = ?;";
	
	private static final String SQL_INIT_PERMISSIONS = 
			"INSERT INTO Permissions (p_u_id, p_acct_id, p_withdraw, p_deposit) VALUES "
		+ 	"(?,?,?,?) "
		+ 	"RETURNING p_id;";
	
	private static final String SQL_RETRIEVE_TRANSACTION_ID = 
			"SELECT"
		+ 	"	t_id, "
		+ 	"	users.*, "
		+ 	"	t_type, "
		+ 	"	t_amount, "
		+ 	"	t_time, "
		+ 	"	src.acct_id 		AS src_acct_id, "
		+ 	"	src.acct_balance 	AS src_acct_balance, "
		+ 	"	src.acct_status 	AS src_acct_status, "
		+ 	"	dst.acct_id 		AS dst_acct_id, "
		+ 	"	dst.acct_balance 	AS dst_acct_balance,"
		+ 	"	dst.acct_status 	AS dst_acct_status "
		+ 	"FROM transactions "
		+ 	"JOIN users 			ON t_u_id "
		+ 	"JOIN accounts AS src 	ON t_src_acct_id = src.acct_id "
		+ 	"JOIN accounts AS dst 	ON t_dst_acct_id = dst.acct_id "
		+ 	"WHERE t_id = ?;";
	
	private static final String SQL_RETRIEVE_TRANSACTION_USER = 
			"SELECT"
		+ 	"	t_id, "
		+ 	"	users.*, "
		+ 	"	t_type, "
		+ 	"	t_amount, "
		+ 	"	t_time, "
		+ 	"	src.acct_id 		AS src_acct_id, "
		+ 	"	src.acct_balance 	AS src_acct_balance, "
		+ 	"	src.acct_status 	AS src_acct_status, "
		+ 	"	dst.acct_id 		AS dst_acct_id, "
		+ 	"	dst.acct_balance 	AS dst_acct_balance,"
		+ 	"	dst.acct_status 	AS dst_acct_status "
		+ 	"FROM transactions "
		+ 	"JOIN users 			ON t_u_id "
		+ 	"JOIN accounts AS src 	ON t_src_acct_id = src.acct_id "
		+ 	"JOIN accounts AS dst 	ON t_dst_acct_id = dst.acct_id "
		+ 	"WHERE u_id = ?;";
	
	private static final String SQL_RETRIEVE_TRANSACTION_ACCOUNT = 
			"SELECT"
		+ 	"	t_id, "
		+ 	"	users.*, "
		+ 	"	t_type, "
		+ 	"	t_amount, "
		+ 	"	t_time, "
		+ 	"	src.acct_id 		AS src_acct_id, "
		+ 	"	src.acct_balance 	AS src_acct_balance, "
		+ 	"	src.acct_status 	AS src_acct_status, "
		+ 	"	dst.acct_id 		AS dst_acct_id, "
		+ 	"	dst.acct_balance 	AS dst_acct_balance,"
		+ 	"	dst.acct_status 	AS dst_acct_status "
		+ 	"FROM transactions "
		+ 	"JOIN users 			ON t_u_id "
		+ 	"JOIN accounts AS src 	ON t_src_acct_id = src.acct_id "
		+ 	"JOIN accounts AS dst 	ON t_dst_acct_id = dst.acct_id "
		+ 	"WHERE src_acct_id = ? OR dst_acct_id = ?;";
	
	private static final String SQL_STORE_TRANSACTION = 
			"INSERT INTO Transactions (t_u_id, t_type, t_amount, t_time, t_src_acct_id, t_dst_acct_id, t_status, t_memo)"
		+ 	"VALUES (?,?,?,?,?,?,?,?) "
		+ 	"RETURNING t_id;";
	
	private static final String SQL_RETRIEVE_USER_USERNAME = 
			"SELECT * FROM Users WHERE u_username = ?";
	
	//Prepared Statements
	private PreparedStatement STATEMENT_STORE_USER;
	private PreparedStatement STATEMENT_RETRIEVE_USER_ID;
	private PreparedStatement STATEMENT_RETRIEVE_USER_CRED;
	
	private PreparedStatement STATEMENT_UPDATE_ACCOUNT;
	private PreparedStatement STATEMENT_INIT_ACCOUNT;
	private PreparedStatement STATEMENT_RETRIEVE_ACCOUNT_ID;
	private PreparedStatement STATEMENT_RETRIEVE_ACCOUNT_STATUS;
	
	private PreparedStatement STATEMENT_RETRIEVE_PERMISSIONS_USER;
	private PreparedStatement STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT;
	private PreparedStatement STATEMENT_RETRIEVE_PERMISSIONS_USER_ACCOUNT;
	private PreparedStatement STATEMENT_STORE_PERMISSIONS;
	private PreparedStatement STATEMENT_INIT_PERMISSIONS;
	
	private PreparedStatement STATEMENT_RETRIEVE_TRANSACTION_ID;
	private PreparedStatement STATEMENT_RETRIEVE_TRANSACTION_USER;
	private PreparedStatement STATEMENT_RETRIEVE_TRANSACTION_ACCOUNT;
	private PreparedStatement STATEMENT_STORE_TRANSACTION;
	
	private PreparedStatement STATEMENT_RETRIEVE_USER_USERNAME;
	
	public BankDatabase (Connection conn) {
		this.conn = conn;		
		this.open();
	}
	
	public BankDatabase (String url, String username, String password) {
		try {
			this.conn = DriverManager.getConnection(url, username, password);
			this.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void open () {
		try {
			System.out.print("Initializing database connection... ");
			
			this.STATEMENT_STORE_USER = conn.prepareStatement(SQL_STORE_USER, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_USER_ID = conn.prepareStatement(SQL_RETRIEVE_USER_ID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_USER_CRED = conn.prepareStatement(SQL_RETRIEVE_USER_CRED, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			

			
			this.STATEMENT_UPDATE_ACCOUNT = conn.prepareStatement(SQL_UPDATE_ACCOUNT, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_INIT_ACCOUNT = conn.prepareStatement(SQL_INIT_ACCOUNT, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_ACCOUNT_ID = conn.prepareStatement(SQL_RETRIEVE_ACCOUNT_ID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_ACCOUNT_STATUS = conn.prepareStatement(SQL_RETRIEVE_ACCOUNT_STATUS, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER = conn.prepareStatement(SQL_RETRIEVE_PERMISSIONS_USER, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT = conn.prepareStatement(SQL_RETRIEVE_PERMISSIONS_ACCOUNT, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER_ACCOUNT = conn.prepareStatement(SQL_RETRIEVE_PERMISSIONS_USER_ACCOUNT, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_STORE_PERMISSIONS = conn.prepareStatement(SQL_STORE_PERMISSIONS, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_INIT_PERMISSIONS = conn.prepareStatement(SQL_INIT_PERMISSIONS);
			
			this.STATEMENT_RETRIEVE_TRANSACTION_ID = conn.prepareStatement(SQL_RETRIEVE_TRANSACTION_ID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_TRANSACTION_USER = conn.prepareStatement(SQL_RETRIEVE_TRANSACTION_USER, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_RETRIEVE_TRANSACTION_ACCOUNT = conn.prepareStatement(SQL_RETRIEVE_TRANSACTION_ACCOUNT, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			this.STATEMENT_STORE_TRANSACTION = conn.prepareStatement(SQL_STORE_TRANSACTION, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			this.STATEMENT_RETRIEVE_USER_USERNAME = conn.prepareStatement(SQL_RETRIEVE_USER_USERNAME, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			System.out.println("DONE.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close () {
		try {
			if (this.conn.isClosed())
				return;

			this.conn.close();
			
		} catch (SQLException e) {
			System.err.println("Something went wrong when closing the database:");
			e.printStackTrace();
		}
	}
	
	public Integer storeUser (User obj) {
		if (obj == null)
			return 0;
		
		try {
			
			this.STATEMENT_STORE_USER.setString	(1, obj.getUsername());
			this.STATEMENT_STORE_USER.setString	(2, obj.getPassword());
			this.STATEMENT_STORE_USER.setString	(3, obj.getAuthorization().toString());
			this.STATEMENT_STORE_USER.setString	(4, obj.getFirstname());
			this.STATEMENT_STORE_USER.setString	(5, obj.getLastname());
			this.STATEMENT_STORE_USER.setString	(6, obj.getSsn());
			this.STATEMENT_STORE_USER.setDate	(7, obj.getBirthdate());
			this.STATEMENT_STORE_USER.setString	(8, obj.getAddress());
			this.STATEMENT_STORE_USER.setString	(9, obj.getPhone());
			
			ResultSet rs = this.STATEMENT_STORE_USER.executeQuery();
			rs.next();
			
			Integer id = rs.getInt(1);
			
			if(id == 0) {
				System.err.println("User storage failed. Rolling back...");
			} else {			
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
		
		ArrayList<User> users = parseUsers(rs);
		
		if (users.isEmpty()) {
			return null;
		} else {
			return users.get(0);
		}
	}
	
	private static ArrayList<User> parseUsers (ResultSet rs) {
		ArrayList<User> output = new ArrayList<>();
		try {
			if ((rs == null) || (!rs.first())) {
				output.add(null);
				return output;
			}
			
			do {
				output.add(
					new User(
						rs.getInt("u_id"),
						rs.getString("u_username"),
						rs.getString("u_password"),
						User.AccessLevel.valueOf(rs.getString("u_auth")),
						rs.getString("u_firstname"),
						rs.getString("u_lastname"),
						rs.getString("u_ssn"),
						rs.getDate("u_birthdate"),
						rs.getString("u_address"),
						rs.getString("u_phone")
					)
				);
			} while(rs.next());
			
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
	
	public ArrayList<Account> fetchAccounts (Account.state status) {
		ResultSet rs;
		try {
			this.STATEMENT_RETRIEVE_ACCOUNT_STATUS.setString(1, status.toString());
			rs = this.STATEMENT_RETRIEVE_ACCOUNT_STATUS.executeQuery();
			
			return BankDatabase.parseAccounts(rs);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public Integer storeAccount (Account obj) {			
		if (obj == null) 
			return 0;
		
		if (obj.getId() == null) {
			obj = this.newAccount();
			return obj.getId();
		}
		
		try {
			
			this.STATEMENT_UPDATE_ACCOUNT.setDouble(1, obj.getBalance());
			this.STATEMENT_UPDATE_ACCOUNT.setString(2, obj.getStatus().toString());
			this.STATEMENT_UPDATE_ACCOUNT.setInt(3, obj.getId());
			
			int result = this.STATEMENT_UPDATE_ACCOUNT.executeUpdate();
			
			if (result != 1) {
				return 0;
			} else {
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
			ResultSet rs = this.STATEMENT_INIT_ACCOUNT.executeQuery();
			rs.next();
			return new Account(
				rs.getInt("acct_id"),
				0.00,
				Account.state.PENDING_APPROVAL
			);
		} catch (SQLException e) {
			e.printStackTrace();			
		}
		return null;
	}
	
	private static ArrayList<Account> parseAccounts (ResultSet rs) {
		return parseAccounts(rs, "");
	}
	
	private static ArrayList<Account> parseAccounts (ResultSet rs, String prefix) {
		
		ArrayList<Account> output = new ArrayList<>(); 
			
		try {
			if ((rs== null) || (!rs.first())){
				output.add(null);
				return output;
			}
				
			
			do {
				output.add(
					new Account (
						rs.getInt(prefix + "acct_id"),
						rs.getDouble(prefix + "acct_balance"),
						Account.state.valueOf(rs.getString(prefix + "acct_status"))
					)
				);
			} while (rs.next());
			
			return output;			
			
		} catch (SQLException e) {
			System.err.println("Something wen wrong when parsing retrieval results " + rs.toString());
			e.printStackTrace();
		}	
		
		
		return null;
	}
	
	public ArrayList<Permissions> fetchPermissions (User u) {
		
		try {
			this.STATEMENT_RETRIEVE_PERMISSIONS_USER.setInt(1, u.getId());
			ResultSet rs = this.STATEMENT_RETRIEVE_PERMISSIONS_USER.executeQuery();
			return BankDatabase.parsePermissions(rs);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public ArrayList<Permissions> fetchPermissions (Account a) {
		
		try {
			this.STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT.setInt(1, a.getId());
			ResultSet rs = this.STATEMENT_RETRIEVE_PERMISSIONS_ACCOUNT.executeQuery();
			return parsePermissions(rs);
			
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
			rs.next();
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
	
	public Integer initPermissions (Permissions p) {
if (p == null) return 0;
		
		try {
			this.STATEMENT_INIT_PERMISSIONS.setInt(1, p.getUser().getId());
			this.STATEMENT_INIT_PERMISSIONS.setInt(2, p.getAccount().getId());
			this.STATEMENT_INIT_PERMISSIONS.setBoolean(3, p.canWithdraw());
			this.STATEMENT_INIT_PERMISSIONS.setBoolean(4, p.canDeposit());
			
			ResultSet rs = this.STATEMENT_INIT_PERMISSIONS.executeQuery();
			rs.next();
			return rs.getInt("p_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Integer storePermissions (Permissions p) {
		
		if (p == null) return 0;
		
		try {
			this.STATEMENT_STORE_PERMISSIONS.setInt(1, p.getUser().getId());
			this.STATEMENT_STORE_PERMISSIONS.setInt(2, p.getAccount().getId());
			this.STATEMENT_STORE_PERMISSIONS.setBoolean(3, p.canWithdraw());
			this.STATEMENT_STORE_PERMISSIONS.setBoolean(4, p.canDeposit());
			
			ResultSet rs = this.STATEMENT_STORE_PERMISSIONS.executeQuery();
			rs.next();
			return rs.getInt("p_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static ArrayList<Permissions> parsePermissions (ResultSet rs) {
		ArrayList<Permissions> output = new ArrayList<>();
		
		try {
			
			ArrayList<User> users = BankDatabase.parseUsers(rs);
			ArrayList<Account> accounts = BankDatabase.parseAccounts(rs);
			
			if ((rs == null) || (!rs.first())) {
				output.add(null);
				return output;
			}
			
			do {
				output.add(
					new Permissions (
						rs.getInt("p_id"),
						users.get(rs.getRow() - 1),
						accounts.get(rs.getRow() - 1),
						rs.getBoolean("p_withdraw"),
						rs.getBoolean("p_deposit")
					)					
				);
			} while (rs.next());
			
		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		}		
		
		return output;
	}
	
	public ArrayList<Transaction> fetchTransactions (Integer id) {
		
		try {
			this.STATEMENT_RETRIEVE_TRANSACTION_ID.setInt(1, id);
			ResultSet rs = this.STATEMENT_RETRIEVE_TRANSACTION_ID.executeQuery();
			return BankDatabase.parseTransactions(rs);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();		
	}
	
	public ArrayList<Transaction> fetchTransactions (User u) {
		
		try {
			this.STATEMENT_RETRIEVE_TRANSACTION_USER.setInt(1, u.getId());
			ResultSet rs = this.STATEMENT_RETRIEVE_TRANSACTION_USER.executeQuery();
			return BankDatabase.parseTransactions(rs);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();		
	}
	
	public ArrayList<Transaction> fetchTransactions (Account a) {
		
		try {
			this.STATEMENT_RETRIEVE_TRANSACTION_ACCOUNT.setInt(1, a.getId());
			this.STATEMENT_RETRIEVE_TRANSACTION_ACCOUNT.setInt(2, a.getId());
			ResultSet rs = this.STATEMENT_RETRIEVE_TRANSACTION_ACCOUNT.executeQuery();
			return BankDatabase.parseTransactions(rs);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();	
	}
	
	public Integer storeTransaction (Transaction t) {
		
		if (t == null)
			return 0;
		
		try {
			
			this.STATEMENT_STORE_TRANSACTION.setInt(1, t.getUser().getId());
			this.STATEMENT_STORE_TRANSACTION.setString(2, t.getType().toString());
			this.STATEMENT_STORE_TRANSACTION.setDouble(3, t.getAmount());
			this.STATEMENT_STORE_TRANSACTION.setTimestamp(4, t.getTime());
			this.STATEMENT_STORE_TRANSACTION.setInt(5, t.getSrc().getId());
			this.STATEMENT_STORE_TRANSACTION.setInt(6, t.getDst().getId());
			this.STATEMENT_STORE_TRANSACTION.setString(7, t.getStatus().toString());
			this.STATEMENT_STORE_TRANSACTION.setString(8, t.getMemo());
			
			ResultSet rs = this.STATEMENT_STORE_TRANSACTION.executeQuery();
			rs.next();
			rs.getInt("t_id");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private static ArrayList<Transaction> parseTransactions (ResultSet rs) {
		
		ArrayList<Transaction> output = new ArrayList<>();
		
		try {
			ArrayList<User> users = BankDatabase.parseUsers(rs);
			ArrayList<Account> src = BankDatabase.parseAccounts(rs, "src_");
			ArrayList<Account> dst = BankDatabase.parseAccounts(rs, "dst_");
			
			if ((rs == null) || (!rs.first())) {
				output.add(null);
				return output;
			}
				

			do {
				output.add(
					new Transaction(
						rs.getInt("t_id"),
						users.get(rs.getRow() - 1), 
						Transaction.Types.valueOf(rs.getString("t_type")), 
						rs.getDouble("t_amount"),
						rs.getTimestamp("t_time"),
						src.get(rs.getRow() - 1), 
						dst.get(rs.getRow() - 1), 
						Transaction.States.valueOf(rs.getString("t_status")), 
						rs.getString("t_memo")
					)
				);
			} while(rs.next());
			
 		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		}
		
		return output;		
	}
	
	public boolean usernameAvailable (String username) {
		try {
			this.STATEMENT_RETRIEVE_USER_USERNAME.setString(1, username);
			return !this.STATEMENT_RETRIEVE_USER_USERNAME.executeQuery().first();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
		
	}
	
}
