package com.github.mkorman9.game.server;

import com.github.mkorman9.game.service.PacketHandler;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.vertx.core.net.NetSocket;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TcpPeerManager {
    @ConfigProperty(name="tcp.server.maxPacketSize")
    int maxPacketSize;

    @Inject
    PacketHandler packetHandler;

    @Inject
    PlayerRegistry playerRegistry;

    public void accept(NetSocket socket) {
        var peer = new TcpPeer(
                packetHandler,
                playerRegistry,
                maxPacketSize
        );

        peer.start(socket);
    }
}
