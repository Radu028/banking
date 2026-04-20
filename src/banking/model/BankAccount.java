package banking.model;

import java.util.Objects;

public abstract class BankAccount implements Comparable<BankAccount> {
    protected String iban;
    protected String ownerId;
    protected String branchCode;
    protected String currency;
    protected double balance;

    public BankAccount(String iban, String ownerId, String branchCode, String currency, double balance) {
        this.iban = iban;
        this.ownerId = ownerId;
        this.branchCode = branchCode;
        this.currency = currency;
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Suma trebuie sa fie pozitiva.");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Suma trebuie sa fie pozitiva.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Fonduri insuficiente.");
        }
        balance -= amount;
    }

    public abstract String getAccountType();

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public int compareTo(BankAccount other) {
        return this.iban.compareTo(other.iban);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BankAccount)) {
            return false;
        }
        BankAccount other = (BankAccount) obj;
        return Objects.equals(iban, other.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban);
    }

    @Override
    public String toString() {
        return getAccountType() + "{iban='" + iban + "', owner='" + ownerId + "', balance=" + balance + " " + currency + "}";
    }
}
