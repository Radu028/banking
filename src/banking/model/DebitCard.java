package banking.model;

public class DebitCard extends Card {
    private boolean contactless;

    public DebitCard(String cardNumber, String accountIban, String holderName, boolean contactless) {
        super(cardNumber, accountIban, holderName);
        this.contactless = contactless;
    }

    public boolean isContactless() {
        return contactless;
    }

    public void setContactless(boolean contactless) {
        this.contactless = contactless;
    }

    @Override
    public String getCardType() {
        return "DebitCard";
    }
}
