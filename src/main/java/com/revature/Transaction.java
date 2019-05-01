package com.revature;

import java.sql.Timestamp;

public class Transaction {
	
	public static enum Types {
		WITHDRAW, DEPOSIT, TRANSFER
	}
	
	public static enum States {
		PENDING, APPROVED, DENIED
	}
	
	private Integer id;
	private User user;	
	private Types type;
	private Double amount;
	private Timestamp time;
	private Account src;
	private Account dst;
	private States status;
	private String memo;
	
	public Transaction (Integer id, User user, Types type, Double amount, Timestamp timestamp, Account src, Account dst, States status, String memo) {
		this.id = id;
		this.user = user;
		this.type = type;
		this.amount = amount;
		this.time = timestamp;
		this.src = src;
		this.dst = dst;
		this.status = status;
		this.memo = memo;
	}

	public Transaction () {
		this(null,null,null,null,null,null,null,null,null);
	}

	
	public States getStatus () { return status; }

	
	public void setStatus (States status) { this.status = status; }

	
	public Integer getId () { return id; }

	
	public User getUser () { return user; }

	
	public Types getType () { return type; }

	public Double getAmount () { return amount; }
	
	public Timestamp getTime () { return this.time; }
	
	public Account getSrc () { return src; }

	public Account getDst () { return dst; }
	
	public String getMemo () { return memo; }
}
