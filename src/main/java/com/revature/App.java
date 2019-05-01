package com.revature;

public class App {
	
	private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/project_0";
	private static final String DEFAULT_USERNAME = "postgres";
	private static final String DEFAULT_PASSWORD = "password";

	public static void main(String[] args) {
		
		Bank bank = new Bank("MyBank", DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
		
		BankUI ui = new BankUI(bank);
		
		while (!ui.select(ui.getCommand()));
		
		ui.close();
		bank.close();
	}

}
