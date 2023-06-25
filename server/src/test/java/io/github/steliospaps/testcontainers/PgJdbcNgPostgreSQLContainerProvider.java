package io.github.steliospaps.testcontainers;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.JdbcDatabaseContainerProvider;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.jdbc.ConnectionUrl;
import org.testcontainers.utility.DockerImageName;

/**
 * Factory for PostgreSQL containers.
 */
public class PgJdbcNgPostgreSQLContainerProvider extends JdbcDatabaseContainerProvider {

    public static final String USER_PARAM = "user";

    public static final String PASSWORD_PARAM = "password";

    @Override
    public boolean supports(String databaseType) {
        return databaseType.equals(PgJdbcNgPostgreSQLContainer.NAME);
    }

    @Override
    public JdbcDatabaseContainer<PgJdbcNgPostgreSQLContainer> newInstance() {
        return newInstance(PostgreSQLContainer.DEFAULT_TAG);
    }

    @Override
    public JdbcDatabaseContainer<PgJdbcNgPostgreSQLContainer> newInstance(String tag) {
        return new PgJdbcNgPostgreSQLContainer(DockerImageName.parse(PostgreSQLContainer.IMAGE).withTag(tag));
    }

    @SuppressWarnings("unchecked")
    @Override
    public JdbcDatabaseContainer<PgJdbcNgPostgreSQLContainer> newInstance(ConnectionUrl connectionUrl) {
        return newInstanceFromConnectionUrl(connectionUrl, USER_PARAM, PASSWORD_PARAM);
    }
}
