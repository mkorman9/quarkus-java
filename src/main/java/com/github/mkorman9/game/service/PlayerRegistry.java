package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationScoped
@Slf4j
public class PlayerRegistry {
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

        log.info("Player connected from {}", context.getSocket().remoteAddress().hostAddress());

        return context;
    }

    public void unregister(PlayerContext context) {
        var removedAny = clients.remove(context.getConnectionId()) != null;
        if (removedAny) {
            if (context.getState() == ConnectionState.PLAY) {
                log.info(
                        "Player {} disconnected while in game: {}",
                        context.getUserInfo().getName(),
                        context.getDisconnectReason().get()
                );
            } else {
                log.info(
                        "Player using {} disconnected: {}",
                        context.getSocket().remoteAddress().hostAddress(),
                        context.getDisconnectReason().get()
                );
            }
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
