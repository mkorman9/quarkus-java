package com.github.mkorman9.core.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlywayRunner {
    @ConfigProperty(name = "flyway.migrate")
    boolean isMigrate;
    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String url;
    @ConfigProperty(name = "quarkus.datasource.username")
    String username;
    @ConfigProperty(name = "quarkus.datasource.password")
    String password;

    public void migrate() {
        if (isMigrate) {
            Flyway.configure()
                    .dataSource(url.replace("vertx-reactive", "jdbc"), username, password)
                    .load()
                    .migrate();
        }
    }
}
