package com.github.mkorman9.game.dto;

import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.mkorman9.game.dto.PlayerDisconnectReason.PEER_RESET;

@Data
@Builder
public class PlayerContext {
    private UUID connectionId;

    @Builder.Default
    private AtomicReference<PlayerDisconnectReason> disconnectReason = new AtomicReference<>(PEER_RESET);

    private NetSocket socket;

    @Builder.Default
    private ConnectionState state = ConnectionState.HANDSHAKE;

    private UserInfo userInfo;

    @Builder.Default
    private HeartbeatInfo heartbeatInfo = new HeartbeatInfo();

    public Future<Void> disconnect() {
        return socket.close();
    }

    public Future<Void> disconnect(PlayerDisconnectReason disconnectReason) {
        this.disconnectReason.set(disconnectReason);
        return disconnect();
    }
}
