SELECT 
	t_id,
	users.*,
	t_type,
	t_amount,
	src.acct_id 		AS src_acct_id,
	src.acct_balance 	AS src_acct_balance,
	src.acct_status 	AS src_acct_status,
	dst.acct_id 		AS dst_acct_id,
	dst.acct_balance 	AS dst_acct_balance,
	dst.acct_status 	AS dst_acct_status
FROM transactions 
JOIN users 				ON t_u_id = u_id 
JOIN accounts AS src 	ON t_src_acct_id = src.acct_id 
JOIN accounts AS dst 	ON t_dst_acct_id = dst.acct_id;