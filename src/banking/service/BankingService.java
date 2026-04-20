package banking.service;

import banking.model.BankAccount;
import banking.model.BankBranch;
import banking.model.BankStatement;
import banking.model.Card;
import banking.model.CreditCard;
import banking.model.CurrentAccount;
import banking.model.Customer;
import banking.model.DebitCard;
import banking.model.SavingsAccount;
import banking.model.Transaction;
import banking.model.TransactionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BankingService {
    private final Map<String, Customer> customers = new HashMap<String, Customer>();
    private final Map<String, BankBranch> branches = new HashMap<String, BankBranch>();
    private final Map<String, BankAccount> accountsByIban = new HashMap<String, BankAccount>();
    private final Set<BankAccount> sortedAccounts = new TreeSet<BankAccount>();
    private final Map<String, List<Card>> cardsByAccount = new HashMap<String, List<Card>>();
    private final List<Transaction> transactions = new ArrayList<Transaction>();
    private int transactionCounter = 1;

    public void addBranch(BankBranch branch) {
        if (branches.containsKey(branch.getBranchCode())) {
            throw new IllegalArgumentException("Sucursala exista deja.");
        }
        branches.put(branch.getBranchCode(), branch);
    }

    public void addCustomer(Customer customer) {
        if (customers.containsKey(customer.getCustomerId())) {
            throw new IllegalArgumentException("Clientul exista deja.");
        }
        customers.put(customer.getCustomerId(), customer);
    }

    public CurrentAccount openCurrentAccount(
            String iban,
            String ownerId,
            String branchCode,
            String currency,
            double initialBalance,
            double monthlyFee
    ) {
        validateNewAccount(iban, ownerId, branchCode);

        CurrentAccount account = new CurrentAccount(iban, ownerId, branchCode, currency, initialBalance, monthlyFee);
        accountsByIban.put(iban, account);
        sortedAccounts.add(account);
        return account;
    }

    public SavingsAccount openSavingsAccount(
            String iban,
            String ownerId,
            String branchCode,
            String currency,
            double initialBalance,
            double interestRate
    ) {
        validateNewAccount(iban, ownerId, branchCode);

        SavingsAccount account = new SavingsAccount(iban, ownerId, branchCode, currency, initialBalance, interestRate);
        accountsByIban.put(iban, account);
        sortedAccounts.add(account);
        return account;
    }

    public DebitCard issueDebitCard(String accountIban, String cardNumber, String holderName, boolean contactless) {
        BankAccount account = getRequiredAccount(accountIban);
        ensureCardNumberIsUnique(cardNumber);

        DebitCard card = new DebitCard(cardNumber, account.getIban(), holderName, contactless);
        addCard(card);
        return card;
    }

    public CreditCard issueCreditCard(String accountIban, String cardNumber, String holderName, double creditLimit) {
        BankAccount account = getRequiredAccount(accountIban);
        ensureCardNumberIsUnique(cardNumber);

        CreditCard card = new CreditCard(cardNumber, account.getIban(), holderName, creditLimit);
        addCard(card);
        return card;
    }

    public void deposit(String iban, double amount, String description) {
        BankAccount account = getRequiredAccount(iban);
        account.deposit(amount);
        recordTransaction(TransactionType.DEPOSIT, null, iban, amount, description);
    }

    public void withdraw(String iban, double amount, String description) {
        BankAccount account = getRequiredAccount(iban);
        account.withdraw(amount);
        recordTransaction(TransactionType.WITHDRAWAL, iban, null, amount, description);
    }

    public void transfer(String sourceIban, String destinationIban, double amount, String description) {
        if (sourceIban.equals(destinationIban)) {
            throw new IllegalArgumentException("Transferul trebuie facut intre conturi diferite.");
        }

        BankAccount sourceAccount = getRequiredAccount(sourceIban);
        BankAccount destinationAccount = getRequiredAccount(destinationIban);

        sourceAccount.withdraw(amount);
        destinationAccount.deposit(amount);
        recordTransaction(TransactionType.TRANSFER, sourceIban, destinationIban, amount, description);
    }

    public void payWithCard(String cardNumber, double amount, String description) {
        Card card = findCardByNumber(cardNumber);

        if (card == null) {
            throw new IllegalArgumentException("Cardul nu exista.");
        }
        if (!card.isActive()) {
            throw new IllegalArgumentException("Cardul este blocat.");
        }

        BankAccount account = getRequiredAccount(card.getAccountIban());
        account.withdraw(amount);
        recordTransaction(TransactionType.CARD_PAYMENT, account.getIban(), null, amount, description);
    }

    public void blockCard(String cardNumber) {
        Card card = findCardByNumber(cardNumber);

        if (card == null) {
            throw new IllegalArgumentException("Cardul nu exista.");
        }

        card.block();
    }

    public void applyInterestToSavingsAccount(String iban) {
        BankAccount account = getRequiredAccount(iban);

        if (!(account instanceof SavingsAccount)) {
            throw new IllegalArgumentException("Dobanda se aplica doar pentru conturile de economii.");
        }

        SavingsAccount savingsAccount = (SavingsAccount) account;
        double interestAmount = savingsAccount.getBalance() * savingsAccount.getInterestRate() / 100.0;
        savingsAccount.deposit(interestAmount);
        recordTransaction(TransactionType.DEPOSIT, null, iban, interestAmount, "Aplicare dobanda");
    }

    public List<BankAccount> getCustomerAccounts(String customerId) {
        List<BankAccount> result = new ArrayList<BankAccount>();

        for (BankAccount account : sortedAccounts) {
            if (account.getOwnerId().equals(customerId)) {
                result.add(account);
            }
        }

        return result;
    }

    public List<BankAccount> getAllAccountsSorted() {
        return new ArrayList<BankAccount>(sortedAccounts);
    }

    public List<Transaction> getTransactionsForAccount(String iban) {
        List<Transaction> result = new ArrayList<Transaction>();

        for (Transaction transaction : transactions) {
            if (iban.equals(transaction.getSourceIban()) || iban.equals(transaction.getDestinationIban())) {
                result.add(transaction);
            }
        }

        return result;
    }

    public BankStatement generateStatement(String iban) {
        BankAccount account = getRequiredAccount(iban);
        List<Transaction> accountTransactions = getTransactionsForAccount(iban);
        double closingBalance = account.getBalance();
        double netChange = 0.0;

        for (Transaction transaction : accountTransactions) {
            if (iban.equals(transaction.getDestinationIban())) {
                netChange += transaction.getAmount();
            }
            if (iban.equals(transaction.getSourceIban())) {
                netChange -= transaction.getAmount();
            }
        }

        double openingBalance = closingBalance - netChange;

        return new BankStatement(
                iban,
                LocalDateTime.now(),
                accountTransactions,
                openingBalance,
                closingBalance
        );
    }

    public double getTotalBankBalance() {
        double total = 0.0;

        for (BankAccount account : sortedAccounts) {
            total += account.getBalance();
        }

        return total;
    }

    private void validateNewAccount(String iban, String ownerId, String branchCode) {
        if (accountsByIban.containsKey(iban)) {
            throw new IllegalArgumentException("Contul exista deja.");
        }
        if (!customers.containsKey(ownerId)) {
            throw new IllegalArgumentException("Clientul nu exista.");
        }
        if (!branches.containsKey(branchCode)) {
            throw new IllegalArgumentException("Sucursala nu exista.");
        }
    }

    private BankAccount getRequiredAccount(String iban) {
        BankAccount account = accountsByIban.get(iban);

        if (account == null) {
            throw new IllegalArgumentException("Contul nu exista.");
        }

        return account;
    }

    private void addCard(Card card) {
        List<Card> cards = cardsByAccount.get(card.getAccountIban());

        if (cards == null) {
            cards = new ArrayList<Card>();
            cardsByAccount.put(card.getAccountIban(), cards);
        }

        cards.add(card);
    }

    private void ensureCardNumberIsUnique(String cardNumber) {
        if (findCardByNumber(cardNumber) != null) {
            throw new IllegalArgumentException("Numarul de card exista deja.");
        }
    }

    private Card findCardByNumber(String cardNumber) {
        for (List<Card> cards : cardsByAccount.values()) {
            for (Card card : cards) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return card;
                }
            }
        }

        return null;
    }

    private void recordTransaction(
            TransactionType type,
            String sourceIban,
            String destinationIban,
            double amount,
            String description
    ) {
        Transaction transaction = new Transaction(
                "T" + transactionCounter,
                type,
                sourceIban,
                destinationIban,
                amount,
                description,
                LocalDateTime.now()
        );

        transactionCounter++;
        transactions.add(transaction);
    }
}
