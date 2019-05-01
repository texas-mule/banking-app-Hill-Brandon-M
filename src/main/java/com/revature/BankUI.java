package com.revature;

import java.io.Console;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;;

public class BankUI {
	private Console c;	
	private Bank b;
	private ArrayList<UIElement> UIMenu;
	public User currentUser;
	
	private Scanner s;
	
	private class UIElement {
		String name;
		String command;
		int accessLevel;
		
		public UIElement (String name, String command, int accessLevel) {
			this.name = name;
			this.command = command;
			this.accessLevel = accessLevel;
		}
	}

	
	public BankUI (Bank b) {
		this.b = b;
		this.c = System.console();
		this.s = new Scanner(System.in);
		this.init();		
	}
	
	private void init () {
		this.UIMenu = new ArrayList<>();
		this.UIMenu.add(new UIElement("Login", "authenticate", 0));
		this.UIMenu.add(new UIElement("Create User", "newUser", 0));
		this.UIMenu.add(new UIElement("Create New Account", "newAccount", 1));
		this.UIMenu.add(new UIElement("View Accounts", "accounts", 1));
		this.UIMenu.add(new UIElement("Withdraw", "withdraw", 1));
		this.UIMenu.add(new UIElement("Deposit", "deposit", 1));
		this.UIMenu.add(new UIElement("Transfer", "transfer", 1));
		this.UIMenu.add(new UIElement("View Client Data", "viewUsers", 2));
		this.UIMenu.add(new UIElement("View Pending Accounts", "pending", 2));
		this.UIMenu.add(new UIElement("Approve/Deny Accounts", "approve", 2));
		this.UIMenu.add(new UIElement("Cancel Accounts", "cancel", 3));
	}
	
	public String prompt (String message) {
		
		System.out.print(message);
		return s.nextLine();
		
	}
	
	public String secretPrompt (String message) {
		return new String(c.readPassword(message));
	}
	
	public String mainMenu () {
		StringBuilder sb = new StringBuilder();
		
		for (UIElement e : UIMenu) {
			if (e.accessLevel > this.currentAccessLevel())
				break;
			
			sb.append(UIMenu.indexOf(e) + ".) " + e.name + "\n");
		}
		
		sb.append("-1.) Exit \n");
		
		return sb.toString();
	}
	
	public Integer getCommand () {
		String input;
		do {
			input = this.prompt(this.mainMenu());
		} while (!StringUtils.isNumeric(input));
		
		return new Integer(input);		
	}
	
	private int currentAccessLevel () {
		if (currentUser == null) return 0;
		
		switch (currentUser.getAuthorization()) {
			case CLIENT:
				return 1;
			case EMPLOYEE:
				return 2;
			case ADMIN:
				return 3;
			
			default:
				return 0;
		}
	}
	
	public boolean select(Integer route) {
		
		switch (route) {
			case 0: 
				this.login();
				break;
				
			case 1:
				this.newUser();
				break;
			
			case 2: 
				this.newAccount();
				break;
				
			case 3:
				this.viewAccounts();
				break;
			
			case 4:
				this.withdraw();
				break;
				
			case 5: 
				this.deposit();
				break;
				
			case 6:
				this.transfer();
				break;
			
			case 7:
				this.viewUsers();
				break;
			
			case 8:
				this.pending();
				break;
			
			case 9:
				this.approve();
				break;
				
			case 10:
				this.cancel();
				break;
				
			case (-1):
				System.out.println("Exiting application...");
				this.currentUser = null;
				return true;
				
			default:
				
		}
		return false;
	}

	private void cancel () {
		
		if (this.currentAccessLevel() < 3) {
			System.err.println("Access denied...");
			return;
		}
		
		try {
			Integer id = new Integer(this.prompt("Account ID: "));
			Account a = this.b.getAccount(id);
			
			if (this.b.cancelAccount(currentUser, a)) {
				System.out.println("Operation successful!");
				return;
			} else {
				System.err.println("The operation failed.");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
	}
	
	public void close() {
		this.s.close();
	}

	private void approve () {
		
		if (this.currentAccessLevel() < 2) {
			System.err.println("Access denied...");
			return;
		}
		
		try {
			Integer id = new Integer(this.prompt("Account ID: "));
			Account a = this.b.getAccount(id);
			
			if (this.b.approveAccount(currentUser, a)) {
				System.out.println("Operation successful!");
				return;
			} else {
				System.err.println("The operation failed.");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
	}

	private void pending () {
		
		if (this.currentAccessLevel() < 2) {
			System.err.println("Access denied...");
			return;
		}
		
		ArrayList<Account> accounts= this.b.getPendingAccounts(currentUser);
		for (Account a : accounts) {
			System.out.println(a.toString());
		}
		
	}

	private void viewUsers () {
		
		if (this.currentAccessLevel() < 2) {
			System.err.println("Access denied...");
			return;
		}
		
		try {
			Integer id = new Integer(this.prompt("ID of desired user"));
			
			User u = this.b.getUserInformation(currentUser, id);
			
			if (u == null) {
				System.err.println("User " + id + " does not exist");
				return;
			}
			
			System.out.println(u.toString());
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
	}

	private void transfer () {

		try {
			Integer src_id = new Integer(this.prompt("Source account ID: "));
			Account src = this.b.getAccount(src_id);
			
			Integer dst_id = new Integer(this.prompt("Destination Account ID: "));
			Account dst = this.b.getAccount(dst_id);
			
			if (src == null || dst == null) {
				System.out.println("Invalid account ID.");
				return;
			}
			
			Integer amount = new Integer(this.prompt("Amount to transfer: "));
			
			String memo = this.prompt("Memo: ");
			
			if (this.b.transfer(currentUser, src, dst, amount, memo)) {
				System.out.println("Operation was successful!");
			} else {
				System.err.println("Operation failed.");
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			
			System.out.println("Withdrawal operation has failed...");
			return;
		}
		
	}

	private void deposit () {
		
		try {
			Integer a_id = new Integer(this.prompt("Account ID: "));
			Account a = this.b.getAccount(a_id);
			
			if (a == null) {
				System.out.println("Invalid account ID.");
				return;
			}
			
			Double amount = new Double(this.prompt("Amount to deposit: "));
			
			if (this.b.deposit(currentUser, a, amount)) {
				System.out.println("Operation was successful!");
			} else {
				System.err.println("Operation failed.");
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			
			System.out.println("Deposit operation has failed...");
			return;
		}
		
	}

	private void withdraw () {
		try {
			Integer a_id = new Integer(this.prompt("Account ID: "));
			Account a = this.b.getAccount(a_id);
			
			if (a == null) {
				System.out.println("Invalid account ID.");
				return;
			}
			
			Integer amount = new Integer(this.prompt("Amount to withdraw: "));
			
			if (this.b.withdraw(currentUser, a, amount)) {
				System.out.println("Operation was successful!");
			} else {
				System.err.println("Operation failed.");
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			
			System.out.println("Withdrawal operation has failed...");
			return;
		}
	}

	private void viewAccounts () {

		ArrayList<Permissions> p = this.b.getAccessibleAccounts(currentUser);
		if (p.isEmpty()) {
			System.out.println("No accounts accessible for this user.");
		}
		
		StringBuilder sb = new StringBuilder();
		for (Permissions i : p) {
			sb.append(i.getAccount().toString());
		}
		
		System.out.println(sb.toString());		
	}

	private void newUser () {
		String username;
		String password;
		do {
			username = this.prompt("Username: ");
		} while (!this.b.usernameAvailable(username));
		
		password = this.prompt("Password: ");
		
		System.out.println("Please confirm credentials...");
		
		if (!(this.prompt("Username: ").equals(username) && this.prompt("Password: ").equals(password))) {
			System.out.println("Account creation failed...");
			return;
		}
		
		try {
			this.currentUser = new User(
				null,
				username,
				password,
				User.AccessLevel.CLIENT,
				this.prompt("First name: "),
				this.prompt("Last name: "),
				this.prompt("SSN: "),
				new Date(new SimpleDateFormat("MM/dd/yyyy").parse(this.prompt("Birthdate (MM/dd/yyyy): ")).getTime()),
				this.prompt("Address: "),
				this.prompt("Phone: ")
			);
		} catch (ParseException e) {
			e.printStackTrace();
			this.currentUser = null;
		}
		
		this.b.storeUser(currentUser);
			
	}

	private void newAccount () {
		
		if (this.currentAccessLevel() == 0) 
			return;
		
		Account a = this.b.newAccount(currentUser);
		
		System.out.println("Account created with ID: " + a.getId());
		System.out.println("Please wait for your account to clear the approval process...");
		
	}

	private void login () {
		String username = this.prompt("Username: ");
		String password = this.prompt("Password: ");
		
		this.currentUser = this.b.authenticate(username, password);
		
		if (this.currentUser == null) {
			System.out.println("Login failed.");
		}
	}
	
}
