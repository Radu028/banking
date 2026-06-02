package banking.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private final Properties properties = new Properties();

    private DatabaseConnection() {
        try (FileInputStream inputStream = new FileInputStream("db.properties")) {
            properties.load(inputStream);
            String driverClass = properties.getProperty("jdbc.driver");
            if (driverClass != null && !driverClass.isEmpty()) {
                Class.forName(driverClass);
            }
        } catch (IOException e) {
            throw new RuntimeException("Nu s-a putut citi fisierul db.properties.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driverul JDBC nu a fost gasit.", e);
        }
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(properties.getProperty("jdbc.url"));

        try (PreparedStatement preparedStatement = connection.prepareStatement("PRAGMA foreign_keys = ON")) {
            preparedStatement.execute();
        }

        return connection;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
