-- Создание таблиц для kursach_vlad
CREATE TABLE IF NOT EXISTS issuer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(255),
    sector VARCHAR(255),
    rating VARCHAR(255),
    description VARCHAR(2000)
);

CREATE TABLE IF NOT EXISTS security (
    id BIGSERIAL PRIMARY KEY,
    issuer_id BIGINT REFERENCES issuer(id),
    ticker VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    security_type VARCHAR(50),
    currency VARCHAR(10),
    last_price NUMERIC(19,4),
    dividend_yield NUMERIC(10,4),
    maturity_date DATE
);

CREATE TABLE IF NOT EXISTS investment_account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    owner_name VARCHAR(255) NOT NULL,
    strategy VARCHAR(255),
    base_currency VARCHAR(10),
    opened_date DATE,
    cash_balance NUMERIC(19,4)
);

CREATE TABLE IF NOT EXISTS trade_transaction (
    id BIGSERIAL PRIMARY KEY,
    security_id BIGINT REFERENCES security(id),
    account_id BIGINT REFERENCES investment_account(id),
    trade_date DATE,
    transaction_type VARCHAR(50),
    quantity NUMERIC(19,4),
    price NUMERIC(19,4),
    fees NUMERIC(19,4),
    notes VARCHAR(1000)
);



