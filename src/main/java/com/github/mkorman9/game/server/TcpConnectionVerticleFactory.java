package com.github.mkorman9.game.server;

import com.github.mkorman9.game.service.PlayerRegistry;
import com.github.mkorman9.game.service.PacketHandler;
import io.vertx.core.net.NetSocket;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TcpConnectionVerticleFactory {
    @ConfigProperty(name="tcp.server.maxPacketSize")
    int maxPacketSize;

    @Inject
    PacketHandler packetHandler;

    @Inject
    PlayerRegistry playerRegistry;

    public TcpConnectionVerticle createVerticle(NetSocket socket) {
        return new TcpConnectionVerticle(
                socket,
                packetHandler,
                playerRegistry,
                maxPacketSize
        );
    }
}
