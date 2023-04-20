package com.github.mkorman9.game.dto;

import io.vertx.core.net.NetSocket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerContext {
    private NetSocket socket;

    private ConnectionState state;
}
