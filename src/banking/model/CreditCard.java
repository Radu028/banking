package banking.model;

public class CreditCard extends Card {
    private double creditLimit;

    public CreditCard(String cardNumber, String accountIban, String holderName, double creditLimit) {
        super(cardNumber, accountIban, holderName);
        this.creditLimit = creditLimit;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Override
    public String getCardType() {
        return "CreditCard";
    }
}
