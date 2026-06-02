package banking.repository.jdbc;

import banking.config.DatabaseConnection;
import banking.model.BankAccount;
import banking.model.CurrentAccount;
import banking.model.SavingsAccount;
import banking.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class BankAccountRepository implements Repository<BankAccount, String> {
    private final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    @Override
    public void save(BankAccount account) throws SQLException {
        String sql = "INSERT INTO accounts(iban, owner_id, branch_code, currency, balance, account_type, monthly_fee, interest_rate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            fillStatement(preparedStatement, account);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public BankAccount findById(String iban) throws SQLException {
        String sql = "SELECT iban, owner_id, branch_code, currency, balance, account_type, monthly_fee, interest_rate "
                + "FROM accounts WHERE iban = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, iban);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<BankAccount> findAll() throws SQLException {
        String sql = "SELECT iban, owner_id, branch_code, currency, balance, account_type, monthly_fee, interest_rate "
                + "FROM accounts ORDER BY iban";
        List<BankAccount> accounts = new ArrayList<BankAccount>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                accounts.add(mapRow(resultSet));
            }
        }

        return accounts;
    }

    public List<BankAccount> findByOwnerId(String ownerId) throws SQLException {
        String sql = "SELECT iban, owner_id, branch_code, currency, balance, account_type, monthly_fee, interest_rate "
                + "FROM accounts WHERE owner_id = ? ORDER BY iban";
        List<BankAccount> accounts = new ArrayList<BankAccount>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    accounts.add(mapRow(resultSet));
                }
            }
        }

        return accounts;
    }

    @Override
    public void update(BankAccount account) throws SQLException {
        String sql = "UPDATE accounts SET owner_id = ?, branch_code = ?, currency = ?, balance = ?, account_type = ?, "
                + "monthly_fee = ?, interest_rate = ? WHERE iban = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, account.getOwnerId());
            preparedStatement.setString(2, account.getBranchCode());
            preparedStatement.setString(3, account.getCurrency());
            preparedStatement.setDouble(4, account.getBalance());
            preparedStatement.setString(5, account.getAccountType());

            if (account instanceof CurrentAccount) {
                preparedStatement.setDouble(6, ((CurrentAccount) account).getMonthlyFee());
            } else {
                preparedStatement.setNull(6, Types.REAL);
            }

            if (account instanceof SavingsAccount) {
                preparedStatement.setDouble(7, ((SavingsAccount) account).getInterestRate());
            } else {
                preparedStatement.setNull(7, Types.REAL);
            }

            preparedStatement.setString(8, account.getIban());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(String iban) throws SQLException {
        String sql = "DELETE FROM accounts WHERE iban = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, iban);
            preparedStatement.executeUpdate();
        }
    }

    private void fillStatement(PreparedStatement preparedStatement, BankAccount account) throws SQLException {
        preparedStatement.setString(1, account.getIban());
        preparedStatement.setString(2, account.getOwnerId());
        preparedStatement.setString(3, account.getBranchCode());
        preparedStatement.setString(4, account.getCurrency());
        preparedStatement.setDouble(5, account.getBalance());
        preparedStatement.setString(6, account.getAccountType());

        if (account instanceof CurrentAccount) {
            preparedStatement.setDouble(7, ((CurrentAccount) account).getMonthlyFee());
        } else {
            preparedStatement.setNull(7, Types.REAL);
        }

        if (account instanceof SavingsAccount) {
            preparedStatement.setDouble(8, ((SavingsAccount) account).getInterestRate());
        } else {
            preparedStatement.setNull(8, Types.REAL);
        }
    }

    private BankAccount mapRow(ResultSet resultSet) throws SQLException {
        String accountType = resultSet.getString("account_type");

        if ("SavingsAccount".equals(accountType)) {
            return new SavingsAccount(
                    resultSet.getString("iban"),
                    resultSet.getString("owner_id"),
                    resultSet.getString("branch_code"),
                    resultSet.getString("currency"),
                    resultSet.getDouble("balance"),
                    resultSet.getDouble("interest_rate")
            );
        }

        return new CurrentAccount(
                resultSet.getString("iban"),
                resultSet.getString("owner_id"),
                resultSet.getString("branch_code"),
                resultSet.getString("currency"),
                resultSet.getDouble("balance"),
                resultSet.getDouble("monthly_fee")
        );
    }
}
