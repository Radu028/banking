package banking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BankStatement {
    private String accountIban;
    private LocalDateTime generatedAt;
    private List<Transaction> transactions;
    private double openingBalance;
    private double closingBalance;

    public BankStatement(
            String accountIban,
            LocalDateTime generatedAt,
            List<Transaction> transactions,
            double openingBalance,
            double closingBalance
    ) {
        this.accountIban = accountIban;
        this.generatedAt = generatedAt;
        this.transactions = new ArrayList<Transaction>(transactions);
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
    }

    public String getAccountIban() {
        return accountIban;
    }

    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<Transaction>(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = new ArrayList<Transaction>(transactions);
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        builder.append("Extras de cont pentru ").append(accountIban).append("\n");
        builder.append("Generat la: ").append(generatedAt.format(formatter)).append("\n");
        builder.append("Sold initial: ").append(openingBalance).append("\n");
        builder.append("Sold final: ").append(closingBalance).append("\n");
        builder.append("Tranzactii:\n");

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            builder.append("- ")
                    .append(transaction.getTimestamp().format(formatter))
                    .append(" | ")
                    .append(transaction.getType())
                    .append(" | ")
                    .append(transaction.getAmount())
                    .append(" | ")
                    .append(transaction.getDescription())
                    .append("\n");
        }

        return builder.toString();
    }
}
