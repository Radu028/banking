# Banking System

A Java banking application built around a relational data model, JDBC repositories, explicit transactions, and an audit trail.

The project models branches, customers, current and savings accounts, debit and credit cards, and account transactions. It started as an object-oriented programming assignment and was extended with persistent storage and reporting.

## Highlights

- Generic `Repository<T, ID>` interface with JDBC implementations for each aggregate.
- Explicit `commit` / `rollback` handling for transfers between accounts.
- Prepared statements and try-with-resources across database operations.
- Current and savings accounts with fees and interest behavior.
- Debit and credit card issuance, card payments, and blocking.
- Thread-safe CSV audit logging for service operations.
- Three SQL reports joining customers, accounts, branches, cards, and transactions.

## Architecture

```text
src/banking/
├── model/          domain objects and report projections
├── repository/     generic repository contract and JDBC implementations
├── service/        banking workflows and transaction boundaries
├── audit/          CSV audit trail
├── config/         database connection and schema initialization
└── Main.java       executable demonstration
```

SQLite is used locally, with the schema defined in [`schema.sql`](schema.sql). The application recreates the schema and seeds a small demonstration dataset on startup.

## Run locally

Requirements: Java 11 or newer.

```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" banking.Main
```

Running the application creates:

- `banking.db` — the local SQLite database;
- `audit.csv` — the service-operation audit trail.

Both files are generated locally and intentionally excluded from version control.

## Example workflows

The executable demonstration creates customers and accounts, issues cards, performs deposits, withdrawals, transfers and card payments, applies savings interest, blocks a card, and prints account statements and joined reports.

## Course context

University of Bucharest — *Advanced Object-Oriented Programming in Java*.
