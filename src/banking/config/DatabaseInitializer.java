package banking.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DatabaseInitializer {
    private DatabaseInitializer() {
    }

    public static void initializeSchema() throws SQLException, IOException {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        String schemaPath = databaseConnection.getProperty("schema.path");
        String sqlContent = Files.readString(Paths.get(schemaPath), StandardCharsets.UTF_8);
        String[] statements = sqlContent.split(";");

        try (Connection connection = databaseConnection.getConnection()) {
            for (int i = 0; i < statements.length; i++) {
                String sql = statements[i].trim();
                if (!sql.isEmpty()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.execute();
                    }
                }
            }
        }
    }
}
