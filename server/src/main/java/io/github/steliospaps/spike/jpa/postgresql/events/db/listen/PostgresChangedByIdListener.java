package io.github.steliospaps.spike.jpa.postgresql.events.db.listen;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.jdbc.PgConnection;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Slf4j
public class PostgresChangedByIdListener {
    private Connection conn;
    private PgConnection pgConn;
    private Thread thread;
    private ApplicationEventPublisher publisher;

    public PostgresChangedByIdListener(DataSource datasource, ApplicationEventPublisher publisher) throws SQLException {
        this.publisher = publisher;
        log.info("created");
        conn = datasource.getConnection();
        pgConn = conn.unwrap(PgConnection.class);

        Statement stmt = conn.createStatement();
        stmt.execute("LISTEN changed_by_id");// TODO: externalise
        stmt.close();

        thread = new Thread(this::run);
        thread.setDaemon(true);
        thread.start();
    }

    private void run() {
        while (!Thread.interrupted()) {
            try {

                log.info("polling for notifications");
                org.postgresql.PGNotification notifications[] = pgConn.getNotifications(10000);

                log.info("polling for notifications got: {} ", notifications == null ? 0 : notifications.length);

                if (notifications != null) {
                    for (int i = 0; i < notifications.length; i++) {
                        log.info("Got notification: {} {}", notifications[i].getName(),
                                notifications[i].getParameter());
                        String[] split = notifications[i].getParameter().split(":", 3);
                        publisher.publishEvent(new ChangedById(split[0], split[1], split[2]));
                    }
                }

            } catch (SQLException sqle) {
                log.error("", sqle);
                return;
            } catch (Exception sqle) {
                log.warn("", sqle);
            }
        }
    }
}
