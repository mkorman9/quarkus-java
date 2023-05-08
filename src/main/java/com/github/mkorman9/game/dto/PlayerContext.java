package com.github.mkorman9.game.dto;

import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
