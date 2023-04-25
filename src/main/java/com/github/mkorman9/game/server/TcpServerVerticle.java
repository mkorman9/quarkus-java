package com.github.mkorman9.game.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
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

    @ConfigProperty(name="tcp.server.receiveBuffer")
    int receiveBufferSize;

    @ConfigProperty(name="tcp.server.sendBuffer")
    int sendBufferSize;

    @Inject
    TcpPeerFactory tcpPeerFactory;

    private NetServer server;

    @Override
    public void start(Promise<Void> promise) {
        var options = new NetServerOptions()
                .setReceiveBufferSize(receiveBufferSize)
                .setSendBufferSize(sendBufferSize);

        server = vertx.createNetServer(options)
                .connectHandler(this::onConnect)
                .exceptionHandler(t -> LOG.error("Exception inside TCP server", t));

        server.listen(port, host)
                .onSuccess(s -> {
                    LOG.info("Started TCP server");
                    promise.complete();
                })
                .onFailure(t -> {
                    LOG.error("Failed to start TCP server", t);
                    promise.fail(t);
                });
    }

    @Override
    public void stop(Promise<Void> promise) {
        server.close()
                .onSuccess(v -> {
                    LOG.info("Stopped TCP server");
                    promise.complete();
                })
                .onFailure(t -> {
                    LOG.error("Failed to stop TCP server", t);
                    promise.fail(t);
                });
    }

    public int getPort() {
        return server.actualPort();
    }

    private void onConnect(NetSocket socket) {
        var peer = tcpPeerFactory.create(socket);
        peer.start();
    }
}
