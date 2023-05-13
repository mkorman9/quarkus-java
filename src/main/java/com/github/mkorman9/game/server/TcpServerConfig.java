package com.github.mkorman9.game.server;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "tcp.server")
public interface TcpServerConfig {
    String host();

    int port();

    int receiveBuffer();

    int sendBuffer();

    int maxPacketSize();

    int heartbeatInterval();

    int heartbeatTimeout();
}
