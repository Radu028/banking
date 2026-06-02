package banking.repository.jdbc;

import banking.config.DatabaseConnection;
import banking.model.Customer;
import banking.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository implements Repository<Customer, String> {
    private final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    @Override
    public void save(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers(customer_id, full_name, email, phone_number) VALUES (?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, customer.getCustomerId());
            preparedStatement.setString(2, customer.getFullName());
            preparedStatement.setString(3, customer.getEmail());
            preparedStatement.setString(4, customer.getPhoneNumber());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Customer findById(String customerId) throws SQLException {
        String sql = "SELECT customer_id, full_name, email, phone_number FROM customers WHERE customer_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getString("customer_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone_number")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT customer_id, full_name, email, phone_number FROM customers ORDER BY customer_id";
        List<Customer> customers = new ArrayList<Customer>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(new Customer(
                        resultSet.getString("customer_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number")
                ));
            }
        }

        return customers;
    }

    @Override
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET full_name = ?, email = ?, phone_number = ? WHERE customer_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, customer.getFullName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.setString(3, customer.getPhoneNumber());
            preparedStatement.setString(4, customer.getCustomerId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, customerId);
            preparedStatement.executeUpdate();
        }
    }
}
