package io.proj3ct.SpringDemoBot.Cash.Rabiit;

import lombok.RequiredArgsConstructor;
import org.postgresql.PGConnection;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
public class PostgresListenerConfig {

    private final DataSource dataSource;
    private final CacheInvalidationSender sender;

    @PostMapping
    public void startListener() {
        Thread listenerThread = new Thread(() -> {
            try (Connection conn = dataSource.getConnection()) {
                PGConnection pgConnection = conn.unwrap(PGConnection.class);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("LISTEN vapecompony_channel");
                }

                while (!Thread.interrupted()) {
                    // Waits for notifications
                    PGConnection pgConn = conn.unwrap(PGConnection.class);
                    org.postgresql.PGNotification[] notifications = pgConn.getNotifications(5000);
                    if (notifications != null) {
                        for (var notification : notifications) {
                            String id = notification.getParameter();
                            sender.sendInvalidation("vapecomponyCache", id);
                        }
                    }

                    Thread.sleep(100); // avoid tight loop
                }
            } catch (Exception e) {
                e.printStackTrace(); // краще логер
            }
        });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }
}
