package banking.model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private TransactionType type;
    private String sourceIban;
    private String destinationIban;
    private double amount;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(
            String transactionId,
            TransactionType type,
            String sourceIban,
            String destinationIban,
            double amount,
            String description,
            LocalDateTime timestamp
    ) {
        this.transactionId = transactionId;
        this.type = type;
        this.sourceIban = sourceIban;
        this.destinationIban = destinationIban;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getSourceIban() {
        return sourceIban;
    }

    public void setSourceIban(String sourceIban) {
        this.sourceIban = sourceIban;
    }

    public String getDestinationIban() {
        return destinationIban;
    }

    public void setDestinationIban(String destinationIban) {
        this.destinationIban = destinationIban;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{id='" + transactionId + "', type=" + type + ", amount=" + amount + ", description='" + description + "'}";
    }
}
