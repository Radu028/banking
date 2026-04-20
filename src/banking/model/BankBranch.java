package banking.model;

public class BankBranch {
    private String branchCode;
    private String city;
    private String address;

    public BankBranch(String branchCode, String city, String address) {
        this.branchCode = branchCode;
        this.city = city;
        this.address = address;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BankBranch{code='" + branchCode + "', city='" + city + "'}";
    }
}
