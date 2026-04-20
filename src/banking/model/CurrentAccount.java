package banking.model;

public class CurrentAccount extends BankAccount {
    private double monthlyFee;

    public CurrentAccount(
            String iban,
            String ownerId,
            String branchCode,
            String currency,
            double balance,
            double monthlyFee
    ) {
        super(iban, ownerId, branchCode, currency, balance);
        this.monthlyFee = monthlyFee;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    @Override
    public String getAccountType() {
        return "CurrentAccount";
    }
}
