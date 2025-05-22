-- Création de la table accounts
CREATE TABLE IF NOT EXISTS accounts (
    acct_no VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table users
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    account_number VARCHAR(10),
    phone_number VARCHAR(15) UNIQUE,
    FOREIGN KEY (account_number) REFERENCES accounts(acct_no) ON DELETE CASCADE
);

-- Création de la table transactions
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id SERIAL PRIMARY KEY,
    account_number VARCHAR(10) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    deposit DECIMAL(10,2) DEFAULT 0.00,
    withdraw DECIMAL(10,2) DEFAULT 0.00,
    balance DECIMAL(10,2) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(acct_no) ON DELETE CASCADE
);

-- Insertion d'un utilisateur admin par défaut
INSERT INTO users (username, password, role) 
VALUES ('admin', 'admin123', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Création d'un trigger pour créer automatiquement un utilisateur lors de la création d'un compte
CREATE OR REPLACE FUNCTION create_user_for_account()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO users (username, password, role, account_number, phone_number)
    VALUES (
        NEW.name, -- Le nom du client comme nom d'utilisateur
        NEW.name || '123', -- Le nom du client + '123' comme mot de passe
        'USER',
        NEW.acct_no,
        NEW.phone_number
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_account_creation
AFTER INSERT ON accounts
FOR EACH ROW
EXECUTE FUNCTION create_user_for_account(); 