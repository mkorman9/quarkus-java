package com.github.mkorman9.game.dto;

import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Data
@Builder
public class PlayerContext {
    private UUID connectionId;

    private AtomicReference<PlayerDisconnectReason> disconnectReason;

    private NetSocket socket;

    private ConnectionState state;

    private UserInfo userInfo;

    private HeartbeatInfo heartbeatInfo;

    public Future<Void> disconnect() {
        return socket.close();
    }

    public Future<Void> disconnect(PlayerDisconnectReason disconnectReason) {
        this.disconnectReason.set(disconnectReason);
        return disconnect();
    }
}
