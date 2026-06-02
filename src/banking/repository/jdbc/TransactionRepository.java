package banking.repository.jdbc;

import banking.config.DatabaseConnection;
import banking.model.Transaction;
import banking.model.TransactionType;
import banking.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements Repository<Transaction, String> {
    private final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    @Override
    public void save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions(transaction_id, type, source_iban, destination_iban, amount, description, timestamp) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transaction.getTransactionId());
            preparedStatement.setString(2, transaction.getType().name());
            preparedStatement.setString(3, transaction.getSourceIban());
            preparedStatement.setString(4, transaction.getDestinationIban());
            preparedStatement.setDouble(5, transaction.getAmount());
            preparedStatement.setString(6, transaction.getDescription());
            preparedStatement.setString(7, transaction.getTimestamp().toString());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Transaction findById(String transactionId) throws SQLException {
        String sql = "SELECT transaction_id, type, source_iban, destination_iban, amount, description, timestamp "
                + "FROM transactions WHERE transaction_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transactionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT transaction_id, type, source_iban, destination_iban, amount, description, timestamp "
                + "FROM transactions ORDER BY timestamp";
        List<Transaction> transactions = new ArrayList<Transaction>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                transactions.add(mapRow(resultSet));
            }
        }

        return transactions;
    }

    public List<Transaction> findByAccountIban(String iban) throws SQLException {
        String sql = "SELECT transaction_id, type, source_iban, destination_iban, amount, description, timestamp "
                + "FROM transactions WHERE source_iban = ? OR destination_iban = ? ORDER BY timestamp";
        List<Transaction> transactions = new ArrayList<Transaction>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, iban);
            preparedStatement.setString(2, iban);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapRow(resultSet));
                }
            }
        }

        return transactions;
    }

    @Override
    public void update(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET type = ?, source_iban = ?, destination_iban = ?, amount = ?, description = ?, timestamp = ? "
                + "WHERE transaction_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setString(2, transaction.getSourceIban());
            preparedStatement.setString(3, transaction.getDestinationIban());
            preparedStatement.setDouble(4, transaction.getAmount());
            preparedStatement.setString(5, transaction.getDescription());
            preparedStatement.setString(6, transaction.getTimestamp().toString());
            preparedStatement.setString(7, transaction.getTransactionId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(String transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transactionId);
            preparedStatement.executeUpdate();
        }
    }

    private Transaction mapRow(ResultSet resultSet) throws SQLException {
        return new Transaction(
                resultSet.getString("transaction_id"),
                TransactionType.valueOf(resultSet.getString("type")),
                resultSet.getString("source_iban"),
                resultSet.getString("destination_iban"),
                resultSet.getDouble("amount"),
                resultSet.getString("description"),
                LocalDateTime.parse(resultSet.getString("timestamp"))
        );
    }
}
