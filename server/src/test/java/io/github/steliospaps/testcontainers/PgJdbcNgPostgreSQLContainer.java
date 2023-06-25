package io.github.steliospaps.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PgJdbcNgPostgreSQLContainer extends PostgreSQLContainer<PgJdbcNgPostgreSQLContainer> {

    public static final String NAME = "pgsql";

    public PgJdbcNgPostgreSQLContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public PgJdbcNgPostgreSQLContainer(String dockerImageName) {
        super(dockerImageName);
    }

    @Override
    public String getJdbcUrl() {
        String original = super.getJdbcUrl();
        String neo = original.replaceAll("^jdbc:postgres:", "jdbc:pgsql:");
        log.info("rewrote {} to {}", original, neo);
        return neo;
    }

    @Override
    public String getDriverClassName() {
        return "com.impossibl.postgres.jdbc.PGDriver";
    }
}