package com.github.mkorman9.util;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.groovy.util.Maps;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresContainer implements QuarkusTestResourceLifecycleManager {
    static PostgreSQLContainer<?> db =
            new PostgreSQLContainer<>("postgres:12")
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Override
    public Map<String, String> start() {
        db.start();

        return Maps.of(
                "quarkus.datasource.jdbc.url", db.getJdbcUrl(),
                "quarkus.datasource.username", db.getUsername(),
                "quarkus.datasource.password", db.getPassword()
        );
    }

    @Override
    public void stop() {
        db.stop();
    }
}
