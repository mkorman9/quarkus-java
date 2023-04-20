package com.github.mkorman9.game;

import io.vertx.core.net.NetSocket;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TcpConnectionVerticleFactory {
    public TcpConnectionVerticle createVerticle(NetSocket socket) {
        return new TcpConnectionVerticle(socket);
    }
}
