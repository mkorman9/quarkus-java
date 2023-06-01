package com.github.mkorman9.game.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Slf4j
public class TcpServerVerticle extends AbstractVerticle {
    @Inject
    TcpServerConfig config;

    @Inject
    TcpPeerFactory tcpPeerFactory;

    private NetServer server;

    @Override
    public void start(Promise<Void> promise) {
        var options = new NetServerOptions()
                .setReceiveBufferSize(config.receiveBuffer())
                .setSendBufferSize(config.sendBuffer());

        server = vertx.createNetServer(options)
                .connectHandler(this::onConnect)
                .exceptionHandler(t -> log.error("Exception inside TCP server", t));

        server.listen(config.port(), config.host())
                .onSuccess(s -> {
                    log.info("Started TCP server");
                    promise.complete();
                })
                .onFailure(t -> {
                    log.error("Failed to start TCP server", t);
                    promise.fail(t);
                });
    }

    @Override
    public void stop(Promise<Void> promise) {
        server.close()
                .onSuccess(v -> {
                    log.info("Stopped TCP server");
                    promise.complete();
                })
                .onFailure(t -> {
                    log.error("Failed to stop TCP server", t);
                    promise.fail(t);
                });
    }

    public int getPort() {
        return server.actualPort();
    }

    private void onConnect(NetSocket socket) {
        socket.pause();
        var peer = tcpPeerFactory.create(socket);
        peer.handle();
    }
}
