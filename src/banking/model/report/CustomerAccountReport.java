package banking.model.report;

public class CustomerAccountReport {
    private String customerName;
    private String iban;
    private String accountType;
    private String branchCity;
    private double balance;

    public CustomerAccountReport(String customerName, String iban, String accountType, String branchCity, double balance) {
        this.customerName = customerName;
        this.iban = iban;
        this.accountType = accountType;
        this.branchCity = branchCity;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return customerName + " | " + iban + " | " + accountType + " | " + branchCity + " | " + balance;
    }
}
