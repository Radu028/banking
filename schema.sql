DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS branches;

CREATE TABLE branches (
    branch_code TEXT PRIMARY KEY,
    city TEXT NOT NULL,
    address TEXT NOT NULL
);

CREATE TABLE customers (
    customer_id TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL,
    phone_number TEXT NOT NULL
);

CREATE TABLE accounts (
    iban TEXT PRIMARY KEY,
    owner_id TEXT NOT NULL,
    branch_code TEXT NOT NULL,
    currency TEXT NOT NULL,
    balance REAL NOT NULL,
    account_type TEXT NOT NULL,
    monthly_fee REAL,
    interest_rate REAL,
    FOREIGN KEY (owner_id) REFERENCES customers(customer_id),
    FOREIGN KEY (branch_code) REFERENCES branches(branch_code)
);

CREATE TABLE cards (
    card_number TEXT PRIMARY KEY,
    account_iban TEXT NOT NULL,
    holder_name TEXT NOT NULL,
    active INTEGER NOT NULL,
    card_type TEXT NOT NULL,
    contactless INTEGER,
    credit_limit REAL,
    FOREIGN KEY (account_iban) REFERENCES accounts(iban)
);

CREATE TABLE transactions (
    transaction_id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    source_iban TEXT,
    destination_iban TEXT,
    amount REAL NOT NULL,
    description TEXT NOT NULL,
    timestamp TEXT NOT NULL,
    FOREIGN KEY (source_iban) REFERENCES accounts(iban),
    FOREIGN KEY (destination_iban) REFERENCES accounts(iban)
);
