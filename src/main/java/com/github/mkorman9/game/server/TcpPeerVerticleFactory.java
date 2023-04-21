package com.github.mkorman9.game.server;

import com.github.mkorman9.game.service.PacketDispatcher;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.vertx.core.net.NetSocket;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TcpPeerVerticleFactory {
    @ConfigProperty(name="tcp.server.maxPacketSize")
    int maxPacketSize;

    @Inject
    PacketDispatcher packetDispatcher;

    @Inject
    PlayerRegistry playerRegistry;

    public TcpPeerVerticle create(NetSocket socket) {
        return new TcpPeerVerticle(
                socket,
                packetDispatcher,
                playerRegistry,
                maxPacketSize
        );
    }
}
