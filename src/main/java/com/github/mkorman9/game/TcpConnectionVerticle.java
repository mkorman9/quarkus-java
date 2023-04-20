package com.github.mkorman9.game;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetSocket;

public class TcpConnectionVerticle extends AbstractVerticle {
    private final NetSocket socket;

    public TcpConnectionVerticle(NetSocket socket) {
        this.socket = socket;
    }

    @Override
    public void start() throws Exception {
    }
}
