CREATE TABLE IF NOT EXISTS Users (
	u_id 		SERIAL 	PRIMARY KEY,
	u_username 	TEXT 	UNIQUE	NOT NULL,
	u_password 	TEXT 			NOT NULL,
	u_auth		TEXT			NOT NULL,
	u_firstname TEXT 			NOT NULL,
	u_lastname 	TEXT 			NOT NULL,
	u_ssn 		TEXT 	UNIQUE	NOT NULL,
	u_birthdate DATE 			NOT NULL,
	u_address 	TEXT 			NOT NULL,
	u_phone 	TEXT 			NOT NULL
);

CREATE TABLE IF NOT EXISTS Accounts (
	acct_id 		SERIAL 				PRIMARY KEY,
	acct_balance 	DOUBLE PRECISION 	NOT NULL 		DEFAULT 0.00,
	acct_status 	TEXT 				NOT NULL
);

CREATE TABLE IF NOT EXISTS Permissions (
	p_id 		SERIAL 					PRIMARY KEY,
	p_u_id 		INTEGER NOT NULL		REFERENCES 	Users(u_id),
	p_acct_id 	INTEGER NOT NULL		REFERENCES 	Accounts(acct_id),
	p_withdraw 	BOOLEAN NOT NULL 		DEFAULT 	FALSE,
	p_deposit 	BOOLEAN NOT NULL 		DEFAULT 	FALSE
);

CREATE TABLE IF NOT EXISTS Transactions (
	t_id 			SERIAL 							PRIMARY KEY,
	t_u_id			INTEGER							REFERENCES 		Users 		(u_id),
	t_type 			TEXT 				NOT NULL,
	t_amount		DOUBLE PRECISION	NOT NULL,
	t_src_acct_id 	INTEGER 			NOT NULL 	REFERENCES 		Accounts	(acct_id),
	t_dst_acct_id 	INTEGER 			NOT NULL 	REFERENCES 		Accounts	(acct_id),
	t_status		TEXT				NOT NULL,
	t_memo			TEXT
);

COMMIT;