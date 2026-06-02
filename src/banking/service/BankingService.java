package banking.service;

import banking.audit.AuditService;
import banking.config.DatabaseConnection;
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
import banking.model.report.CardReport;
import banking.model.report.CustomerAccountReport;
import banking.model.report.TransactionReport;
import banking.repository.jdbc.BankAccountRepository;
import banking.repository.jdbc.BankBranchRepository;
import banking.repository.jdbc.CardRepository;
import banking.repository.jdbc.CustomerRepository;
import banking.repository.jdbc.TransactionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

public class BankingService {
    private final BankBranchRepository bankBranchRepository = new BankBranchRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final BankAccountRepository bankAccountRepository = new BankAccountRepository();
    private final CardRepository cardRepository = new CardRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final AuditService auditService = AuditService.getInstance();
    private final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    public void addBranch(BankBranch branch) throws SQLException {
        bankBranchRepository.save(branch);
        auditService.logAction("addBranch");
    }

    public void addCustomer(Customer customer) throws SQLException {
        customerRepository.save(customer);
        auditService.logAction("addCustomer");
    }

    public CurrentAccount openCurrentAccount(
            String iban,
            String ownerId,
            String branchCode,
            String currency,
            double initialBalance,
            double monthlyFee
    ) throws SQLException {
        CurrentAccount account = new CurrentAccount(iban, ownerId, branchCode, currency, initialBalance, monthlyFee);
        bankAccountRepository.save(account);
        auditService.logAction("openCurrentAccount");
        return account;
    }

    public SavingsAccount openSavingsAccount(
            String iban,
            String ownerId,
            String branchCode,
            String currency,
            double initialBalance,
            double interestRate
    ) throws SQLException {
        SavingsAccount account = new SavingsAccount(iban, ownerId, branchCode, currency, initialBalance, interestRate);
        bankAccountRepository.save(account);
        auditService.logAction("openSavingsAccount");
        return account;
    }

    public DebitCard issueDebitCard(String accountIban, String cardNumber, String holderName, boolean contactless) throws SQLException {
        if (bankAccountRepository.findById(accountIban) == null) {
            throw new IllegalArgumentException("Contul nu exista.");
        }

        DebitCard card = new DebitCard(cardNumber, accountIban, holderName, contactless);
        cardRepository.save(card);
        auditService.logAction("issueDebitCard");
        return card;
    }

    public CreditCard issueCreditCard(String accountIban, String cardNumber, String holderName, double creditLimit) throws SQLException {
        if (bankAccountRepository.findById(accountIban) == null) {
            throw new IllegalArgumentException("Contul nu exista.");
        }

        CreditCard card = new CreditCard(cardNumber, accountIban, holderName, creditLimit);
        cardRepository.save(card);
        auditService.logAction("issueCreditCard");
        return card;
    }

    public void deposit(String iban, double amount, String description) throws SQLException {
        BankAccount account = getRequiredAccount(iban);
        account.deposit(amount);
        bankAccountRepository.update(account);
        transactionRepository.save(newTransaction(TransactionType.DEPOSIT, null, iban, amount, description));
        auditService.logAction("deposit");
    }

    public void withdraw(String iban, double amount, String description) throws SQLException {
        BankAccount account = getRequiredAccount(iban);
        account.withdraw(amount);
        bankAccountRepository.update(account);
        transactionRepository.save(newTransaction(TransactionType.WITHDRAWAL, iban, null, amount, description));
        auditService.logAction("withdraw");
    }

    public void transfer(String sourceIban, String destinationIban, double amount, String description) throws SQLException {
        String balanceSql = "SELECT balance FROM accounts WHERE iban = ?";
        String updateSql = "UPDATE accounts SET balance = ? WHERE iban = ?";
        String insertTransactionSql = "INSERT INTO transactions(transaction_id, type, source_iban, destination_iban, amount, description, timestamp) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        if (sourceIban.equals(destinationIban)) {
            throw new IllegalArgumentException("Transferul trebuie facut intre conturi diferite.");
        }

        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                double sourceBalance = getBalance(connection, balanceSql, sourceIban);
                double destinationBalance = getBalance(connection, balanceSql, destinationIban);

                if (amount <= 0) {
                    throw new IllegalArgumentException("Suma trebuie sa fie pozitiva.");
                }
                if (sourceBalance < amount) {
                    throw new IllegalArgumentException("Fonduri insuficiente.");
                }

                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setDouble(1, sourceBalance - amount);
                    updateStatement.setString(2, sourceIban);
                    updateStatement.executeUpdate();

                    updateStatement.setDouble(1, destinationBalance + amount);
                    updateStatement.setString(2, destinationIban);
                    updateStatement.executeUpdate();
                }

                try (PreparedStatement transactionStatement = connection.prepareStatement(insertTransactionSql)) {
                    transactionStatement.setString(1, UUID.randomUUID().toString());
                    transactionStatement.setString(2, TransactionType.TRANSFER.name());
                    transactionStatement.setString(3, sourceIban);
                    transactionStatement.setString(4, destinationIban);
                    transactionStatement.setDouble(5, amount);
                    transactionStatement.setString(6, description);
                    transactionStatement.setString(7, LocalDateTime.now().toString());
                    transactionStatement.executeUpdate();
                }

                connection.commit();
                auditService.logAction("transfer");
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void payWithCard(String cardNumber, double amount, String description) throws SQLException {
        Card card = cardRepository.findById(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException("Cardul nu exista.");
        }
        if (!card.isActive()) {
            throw new IllegalArgumentException("Cardul este blocat.");
        }

        BankAccount account = getRequiredAccount(card.getAccountIban());
        account.withdraw(amount);
        bankAccountRepository.update(account);
        transactionRepository.save(newTransaction(TransactionType.CARD_PAYMENT, account.getIban(), null, amount, description));
        auditService.logAction("payWithCard");
    }

    public void blockCard(String cardNumber) throws SQLException {
        Card card = cardRepository.findById(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException("Cardul nu exista.");
        }

        card.block();
        cardRepository.update(card);
        auditService.logAction("blockCard");
    }

    public void applyInterestToSavingsAccount(String iban) throws SQLException {
        BankAccount account = getRequiredAccount(iban);
        if (!(account instanceof SavingsAccount)) {
            throw new IllegalArgumentException("Dobanda se aplica doar pe cont de economii.");
        }

        SavingsAccount savingsAccount = (SavingsAccount) account;
        double interestAmount = savingsAccount.getBalance() * savingsAccount.getInterestRate() / 100.0;
        savingsAccount.deposit(interestAmount);
        bankAccountRepository.update(savingsAccount);
        transactionRepository.save(newTransaction(TransactionType.DEPOSIT, null, iban, interestAmount, "Aplicare dobanda"));
        auditService.logAction("applyInterestToSavingsAccount");
    }

    public List<BankAccount> getCustomerAccounts(String customerId) throws SQLException {
        TreeSet<BankAccount> sortedAccounts = new TreeSet<BankAccount>(bankAccountRepository.findByOwnerId(customerId));
        auditService.logAction("getCustomerAccounts");
        return new ArrayList<BankAccount>(sortedAccounts);
    }

    public List<BankAccount> getAllAccountsSorted() throws SQLException {
        TreeSet<BankAccount> sortedAccounts = new TreeSet<BankAccount>(bankAccountRepository.findAll());
        auditService.logAction("getAllAccountsSorted");
        return new ArrayList<BankAccount>(sortedAccounts);
    }

    public List<Transaction> getTransactionsForAccount(String iban) throws SQLException {
        auditService.logAction("getTransactionsForAccount");
        return transactionRepository.findByAccountIban(iban);
    }

    public BankStatement generateStatement(String iban) throws SQLException {
        BankAccount account = getRequiredAccount(iban);
        List<Transaction> accountTransactions = transactionRepository.findByAccountIban(iban);
        double closingBalance = account.getBalance();
        double netChange = 0.0;

        for (int i = 0; i < accountTransactions.size(); i++) {
            Transaction transaction = accountTransactions.get(i);
            if (iban.equals(transaction.getDestinationIban())) {
                netChange += transaction.getAmount();
            }
            if (iban.equals(transaction.getSourceIban())) {
                netChange -= transaction.getAmount();
            }
        }

        auditService.logAction("generateStatement");
        return new BankStatement(iban, LocalDateTime.now(), accountTransactions, closingBalance - netChange, closingBalance);
    }

    public double getTotalBankBalance() throws SQLException {
        double total = 0.0;
        List<BankAccount> accounts = bankAccountRepository.findAll();

        for (int i = 0; i < accounts.size(); i++) {
            total += accounts.get(i).getBalance();
        }

        auditService.logAction("getTotalBankBalance");
        return total;
    }

    public List<CustomerAccountReport> getCustomerAccountReports() throws SQLException {
        String sql = "SELECT c.full_name, a.iban, a.account_type, b.city, a.balance "
                + "FROM customers c "
                + "JOIN accounts a ON c.customer_id = a.owner_id "
                + "JOIN branches b ON a.branch_code = b.branch_code "
                + "ORDER BY c.full_name, a.iban";
        List<CustomerAccountReport> reports = new ArrayList<CustomerAccountReport>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                reports.add(new CustomerAccountReport(
                        resultSet.getString("full_name"),
                        resultSet.getString("iban"),
                        resultSet.getString("account_type"),
                        resultSet.getString("city"),
                        resultSet.getDouble("balance")
                ));
            }
        }

        auditService.logAction("getCustomerAccountReports");
        return reports;
    }

    public List<CardReport> getCardReports() throws SQLException {
        String sql = "SELECT cd.card_number, cd.card_type, c.full_name, a.iban, cd.active "
                + "FROM cards cd "
                + "JOIN accounts a ON cd.account_iban = a.iban "
                + "JOIN customers c ON a.owner_id = c.customer_id "
                + "ORDER BY cd.card_number";
        List<CardReport> reports = new ArrayList<CardReport>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                reports.add(new CardReport(
                        resultSet.getString("card_number"),
                        resultSet.getString("card_type"),
                        resultSet.getString("full_name"),
                        resultSet.getString("iban"),
                        resultSet.getInt("active") == 1
                ));
            }
        }

        auditService.logAction("getCardReports");
        return reports;
    }

    public List<TransactionReport> getTransactionReports() throws SQLException {
        String sql = "SELECT t.transaction_id, t.type, COALESCE(sc.full_name, '-') AS source_owner, "
                + "COALESCE(dc.full_name, '-') AS destination_owner, t.amount "
                + "FROM transactions t "
                + "LEFT JOIN accounts sa ON t.source_iban = sa.iban "
                + "LEFT JOIN customers sc ON sa.owner_id = sc.customer_id "
                + "LEFT JOIN accounts da ON t.destination_iban = da.iban "
                + "LEFT JOIN customers dc ON da.owner_id = dc.customer_id "
                + "ORDER BY t.timestamp";
        List<TransactionReport> reports = new ArrayList<TransactionReport>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                reports.add(new TransactionReport(
                        resultSet.getString("transaction_id"),
                        resultSet.getString("type"),
                        resultSet.getString("source_owner"),
                        resultSet.getString("destination_owner"),
                        resultSet.getDouble("amount")
                ));
            }
        }

        auditService.logAction("getTransactionReports");
        return reports;
    }

    private BankAccount getRequiredAccount(String iban) throws SQLException {
        BankAccount account = bankAccountRepository.findById(iban);
        if (account == null) {
            throw new IllegalArgumentException("Contul nu exista.");
        }
        return account;
    }

    private Transaction newTransaction(
            TransactionType type,
            String sourceIban,
            String destinationIban,
            double amount,
            String description
    ) {
        return new Transaction(
                UUID.randomUUID().toString(),
                type,
                sourceIban,
                destinationIban,
                amount,
                description,
                LocalDateTime.now()
        );
    }

    private double getBalance(Connection connection, String balanceSql, String iban) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(balanceSql)) {
            preparedStatement.setString(1, iban);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
            }
        }

        throw new IllegalArgumentException("Contul " + iban + " nu exista.");
    }
}
