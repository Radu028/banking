package banking;

import banking.model.BankBranch;
import banking.model.BankStatement;
import banking.model.CurrentAccount;
import banking.model.Customer;
import banking.model.SavingsAccount;
import banking.service.BankingService;

public class Main {
    public static void main(String[] args) {
        BankingService bankingService = new BankingService();

        bankingService.addBranch(new BankBranch("B001", "Bucuresti", "Bd. Unirii 10"));
        bankingService.addBranch(new BankBranch("B002", "Cluj-Napoca", "Str. Memorandumului 21"));

        bankingService.addCustomer(new Customer("C001", "Andrei Popescu", "andrei@mail.com", "0711111111"));
        bankingService.addCustomer(new Customer("C002", "Maria Ionescu", "maria@mail.com", "0722222222"));

        CurrentAccount currentAccount = bankingService.openCurrentAccount(
                "RO49BANK0000000000000001",
                "C001",
                "B001",
                "RON",
                2500.0,
                12.0
        );

        SavingsAccount savingsAccount = bankingService.openSavingsAccount(
                "RO49BANK0000000000000002",
                "C001",
                "B001",
                "RON",
                5000.0,
                3.5
        );

        bankingService.openCurrentAccount(
                "RO49BANK0000000000000003",
                "C002",
                "B002",
                "RON",
                1800.0,
                10.0
        );

        bankingService.issueDebitCard(
                currentAccount.getIban(),
                "4444555566667777",
                "Andrei Popescu",
                true
        );

        bankingService.issueCreditCard(
                currentAccount.getIban(),
                "5555666677778888",
                "Andrei Popescu",
                4000.0
        );

        bankingService.deposit("RO49BANK0000000000000001", 400.0, "Depunere salariu");
        bankingService.withdraw("RO49BANK0000000000000001", 150.0, "Retragere ATM");
        bankingService.transfer(
                "RO49BANK0000000000000001",
                "RO49BANK0000000000000003",
                300.0,
                "Transfer catre Maria"
        );
        bankingService.payWithCard("4444555566667777", 120.0, "Plata supermarket");
        bankingService.applyInterestToSavingsAccount(savingsAccount.getIban());
        bankingService.blockCard("5555666677778888");

        System.out.println("Conturile clientului C001:");
        for (int i = 0; i < bankingService.getCustomerAccounts("C001").size(); i++) {
            System.out.println(bankingService.getCustomerAccounts("C001").get(i));
        }

        System.out.println();
        System.out.println("Conturi sortate dupa IBAN:");
        for (int i = 0; i < bankingService.getAllAccountsSorted().size(); i++) {
            System.out.println(bankingService.getAllAccountsSorted().get(i));
        }

        System.out.println();
        BankStatement statement = bankingService.generateStatement("RO49BANK0000000000000001");
        System.out.println(statement);

        System.out.println();
        System.out.println("Sold total in sistem: " + bankingService.getTotalBankBalance() + " RON");
    }
}
