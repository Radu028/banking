package banking.model;

public class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(
            String iban,
            String ownerId,
            String branchCode,
            String currency,
            double balance,
            double interestRate
    ) {
        super(iban, ownerId, branchCode, currency, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String getAccountType() {
        return "SavingsAccount";
    }
}
