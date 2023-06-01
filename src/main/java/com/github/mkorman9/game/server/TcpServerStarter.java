package com.github.mkorman9.game.server;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Slf4j
public class TcpServerStarter {
    @Inject
    Vertx vertx;

    @Inject
    TcpServerVerticle tcpServerVerticle;

    public void startup(@Observes StartupEvent startupEvent) {
        vertx.deployVerticle(tcpServerVerticle)
                .onFailure(e -> log.error("Failed to deploy TcpServerVerticle", e));
    }
}
