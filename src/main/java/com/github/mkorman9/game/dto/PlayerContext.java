package com.github.mkorman9.game.dto;

import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PlayerContext {
    private NetSocket socket;

    private ConnectionState state;

    private String userName;

    private UUID userId;

    public Future<Void> disconnect() {
        return socket.close();
    }
}
