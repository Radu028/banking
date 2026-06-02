package banking.audit;

import banking.config.DatabaseConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public final class AuditService {
    private static final AuditService INSTANCE = new AuditService();
    private final Path auditPath;

    private AuditService() {
        String filePath = DatabaseConnection.getInstance().getProperty("audit.path");
        this.auditPath = Paths.get(filePath);
    }

    public static AuditService getInstance() {
        return INSTANCE;
    }

    public synchronized void logAction(String actionName) {
        boolean fileExists = Files.exists(auditPath);

        try (BufferedWriter writer = Files.newBufferedWriter(
                auditPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            if (!fileExists) {
                writer.write("action_name,timestamp,thread_name");
                writer.newLine();
            }

            writer.write(actionName + "," + LocalDateTime.now() + "," + Thread.currentThread().getName());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Nu s-a putut scrie in audit.csv.", e);
        }
    }
}
