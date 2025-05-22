CREATE DATABASE IF NOT EXISTS bank;
USE bank;


CREATE TABLE IF NOT EXISTS tblAccount (
    acct_no VARCHAR(20) PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(8) NOT NULL CHECK (phone_number REGEXP '^[234][0-9]{7}$'),
    sex VARCHAR(10) NOT NULL,
    branch VARCHAR(50) NOT NULL,
    initial_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGINT PRIMARY KEY,
    acct_no VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    deposit DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    withdraw DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    balance DECIMAL(15,2) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (acct_no) REFERENCES tblAccount(acct_no) ON DELETE RESTRICT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    account_number VARCHAR(20),
    phone_number VARCHAR(8),
    FOREIGN KEY (account_number) REFERENCES tblAccount(acct_no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO users (username, password, role) 
VALUES ('admin', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;


CREATE INDEX idx_account_number ON tblAccount(acct_no);
CREATE INDEX idx_phone_number ON tblAccount(phone_number);
CREATE INDEX idx_transaction_date ON transactions(date);
CREATE INDEX idx_transaction_account ON transactions(acct_no);
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_user_phone ON users(phone_number);

