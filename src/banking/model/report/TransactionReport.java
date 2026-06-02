package banking.model.report;

public class TransactionReport {
    private String transactionId;
    private String type;
    private String sourceOwner;
    private String destinationOwner;
    private double amount;

    public TransactionReport(String transactionId, String type, String sourceOwner, String destinationOwner, double amount) {
        this.transactionId = transactionId;
        this.type = type;
        this.sourceOwner = sourceOwner;
        this.destinationOwner = destinationOwner;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return transactionId + " | " + type + " | " + sourceOwner + " -> " + destinationOwner + " | " + amount;
    }
}
