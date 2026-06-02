package banking.model.report;

public class CardReport {
    private String cardNumber;
    private String cardType;
    private String customerName;
    private String iban;
    private boolean active;

    public CardReport(String cardNumber, String cardType, String customerName, String iban, boolean active) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.customerName = customerName;
        this.iban = iban;
        this.active = active;
    }

    @Override
    public String toString() {
        return cardNumber + " | " + cardType + " | " + customerName + " | " + iban + " | activ=" + active;
    }
}
