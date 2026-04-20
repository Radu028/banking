package banking.model;

import java.util.Objects;

public abstract class Card {
    protected String cardNumber;
    protected String accountIban;
    protected String holderName;
    protected boolean active;

    public Card(String cardNumber, String accountIban, String holderName) {
        this.cardNumber = cardNumber;
        this.accountIban = accountIban;
        this.holderName = holderName;
        this.active = true;
    }

    public void block() {
        this.active = false;
    }

    public void unblock() {
        this.active = true;
    }

    public abstract String getCardType();

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAccountIban() {
        return accountIban;
    }

    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Card)) {
            return false;
        }
        Card other = (Card) obj;
        return Objects.equals(cardNumber, other.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public String toString() {
        return getCardType() + "{number='" + cardNumber + "', account='" + accountIban + "', active=" + active + "}";
    }
}
