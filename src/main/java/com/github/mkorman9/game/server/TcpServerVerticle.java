package com.github.mkorman9.game.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetSocket;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TcpServerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(TcpServerStarter.class);

    @ConfigProperty(name="tcp.server.host")
    String host;

    @ConfigProperty(name="tcp.server.port")
    int port;

    @Inject
    TcpPeerFactory tcpPeerFactory;

    @Override
    public void start() throws Exception {
        vertx.createNetServer()
                .connectHandler(this::connectHandler)
                .exceptionHandler(t -> LOG.error("Exception inside TCP server", t))
                .listen(port, host)
                .onSuccess(s -> LOG.info("Started TCP server"))
                .onFailure(t -> LOG.error("Failed to start TCP server", t));
    }

    private void connectHandler(NetSocket socket) {
        var peer = tcpPeerFactory.create(socket);
        peer.start();
    }
}
