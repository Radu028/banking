package banking.repository.jdbc;

import banking.config.DatabaseConnection;
import banking.model.BankBranch;
import banking.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankBranchRepository implements Repository<BankBranch, String> {
    private final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    @Override
    public void save(BankBranch branch) throws SQLException {
        String sql = "INSERT INTO branches(branch_code, city, address) VALUES (?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, branch.getBranchCode());
            preparedStatement.setString(2, branch.getCity());
            preparedStatement.setString(3, branch.getAddress());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public BankBranch findById(String branchCode) throws SQLException {
        String sql = "SELECT branch_code, city, address FROM branches WHERE branch_code = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, branchCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new BankBranch(
                            resultSet.getString("branch_code"),
                            resultSet.getString("city"),
                            resultSet.getString("address")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<BankBranch> findAll() throws SQLException {
        String sql = "SELECT branch_code, city, address FROM branches ORDER BY branch_code";
        List<BankBranch> branches = new ArrayList<BankBranch>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                branches.add(new BankBranch(
                        resultSet.getString("branch_code"),
                        resultSet.getString("city"),
                        resultSet.getString("address")
                ));
            }
        }

        return branches;
    }

    @Override
    public void update(BankBranch branch) throws SQLException {
        String sql = "UPDATE branches SET city = ?, address = ? WHERE branch_code = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, branch.getCity());
            preparedStatement.setString(2, branch.getAddress());
            preparedStatement.setString(3, branch.getBranchCode());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(String branchCode) throws SQLException {
        String sql = "DELETE FROM branches WHERE branch_code = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, branchCode);
            preparedStatement.executeUpdate();
        }
    }
}
