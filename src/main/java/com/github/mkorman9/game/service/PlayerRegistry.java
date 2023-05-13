package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.HeartbeatInfo;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.PlayerDisconnectReason;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@ApplicationScoped
public class PlayerRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerRegistry.class);

    private final ConcurrentHashMap<UUID, PlayerContext> clients = new ConcurrentHashMap<>();

    public PlayerContext register(NetSocket socket) {
        var connectionId = UUID.randomUUID();
        var context = PlayerContext.builder()
                .connectionId(connectionId)
                .disconnectReason(new AtomicReference<>(PlayerDisconnectReason.PEER_RESET))
                .socket(socket)
                .state(ConnectionState.HANDSHAKE)
                .heartbeatInfo(new HeartbeatInfo())
                .build();

        if (clients.putIfAbsent(connectionId, context) != null) {
            return register(socket);
        }

        return context;
    }

    public void unregister(PlayerContext context) {
        if (clients.remove(context.getConnectionId()) != null) {
            LOG.info("Player {} disconnected: {}", context.getUserId(), context.getDisconnectReason().get());
        }
    }

    public void forEach(Consumer<PlayerContext> consumer) {
        clients.forEach((cid, context) -> {
            consumer.accept(context);
        });
    }

    public void forEachInPlay(Consumer<PlayerContext> consumer) {
        clients.forEach((cid, context) -> {
            if (context.getState() == ConnectionState.PLAY) {
                consumer.accept(context);
            }
        });
    }
}
