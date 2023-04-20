package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import io.vertx.core.net.NetSocket;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationScoped
public class PlayerRegistry {
    private final ConcurrentHashMap<PlayerContext, Boolean> clients = new ConcurrentHashMap<>();

    public PlayerContext register(NetSocket socket) {
        var context = PlayerContext.builder()
                .socket(socket)
                .state(ConnectionState.HANDSHAKE)
                .build();
        clients.put(context, true);
        return context;
    }

    public void unregister(PlayerContext context) {
        clients.remove(context);
    }

    public void forEach(Consumer<PlayerContext> consumer) {
        clients.forEach((client, v) -> {
            consumer.accept(client);
        });
    }
}
