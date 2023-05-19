package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationScoped
public class PlayerRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerRegistry.class);

    private final ConcurrentHashMap<UUID, PlayerContext> clients = new ConcurrentHashMap<>();

    public PlayerContext register(NetSocket socket) {
        var connectionId = UUID.randomUUID();
        var context = PlayerContext.builder()
                .connectionId(connectionId)
                .socket(socket)
                .build();

        if (clients.putIfAbsent(connectionId, context) != null) {
            return register(socket);
        }

        return context;
    }

    public void unregister(PlayerContext context) {
        var removedAny = clients.remove(context.getConnectionId()) != null;
        if (removedAny && context.getState() == ConnectionState.PLAY) {
            LOG.info(
                    "Player {} disconnected: {}",
                    context.getUserInfo().getName(),
                    context.getDisconnectReason().get()
            );
        }
    }

    public void forEachInPlay(Consumer<PlayerContext> consumer) {
        clients.forEach((cid, context) -> {
            if (context.getState() == ConnectionState.PLAY) {
                consumer.accept(context);
            }
        });
    }

    public int countInPlay() {
        return clients.reduceValuesToInt(
                Long.MAX_VALUE,
                ctx -> ctx.getState() == ConnectionState.PLAY ? 1 : 0,
                0,
                Integer::sum
        );
    }
}
