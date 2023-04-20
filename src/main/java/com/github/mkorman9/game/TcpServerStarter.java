package com.github.mkorman9.game;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class TcpServerStarter {
    private static final Logger LOG = LoggerFactory.getLogger(TcpServerStarter.class);

    @Inject
    Vertx vertx;

    @Inject
    TcpServerVerticle tcpServerVerticle;

    public void startup(@Observes StartupEvent startupEvent) {
        vertx.deployVerticle(tcpServerVerticle)
                .onFailure(e -> Log.error("Failed to deploy TcpServerVerticle", e));
    }
}
