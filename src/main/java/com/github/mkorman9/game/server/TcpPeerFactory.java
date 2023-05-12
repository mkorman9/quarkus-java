package com.github.mkorman9.game.server;

import com.github.mkorman9.game.service.PacketDispatcher;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.vertx.core.net.NetSocket;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TcpPeerFactory {
    @Inject
    TcpServerConfig config;

    @Inject
    PacketDispatcher packetDispatcher;

    @Inject
    PlayerRegistry playerRegistry;

    public TcpPeer create(NetSocket socket) {
        return new TcpPeer(
                socket,
                packetDispatcher,
                playerRegistry,
                config.maxPacketSize()
        );
    }
}
