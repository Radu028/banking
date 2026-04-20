package banking.model;

import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BankBranch)) {
            return false;
        }
        BankBranch other = (BankBranch) obj;
        return Objects.equals(branchCode, other.branchCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchCode);
    }

    @Override
    public String toString() {
        return "BankBranch{code='" + branchCode + "', city='" + city + "'}";
    }
}
