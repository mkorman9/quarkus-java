package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.PlayerContext;
import io.vertx.core.buffer.Buffer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PacketHandler {
    public void handle(PlayerContext context, Buffer packet) {
        var packetId = 0;
        var payload = packet;

        try {
            packetId = packet.getIntLE(0);
            payload = packet.getBuffer(4, packet.length());
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        switch (context.getState()) {
            case HANDSHAKE -> handleHandshake(context, packetId, payload);
            case LOGIN -> handleLogin(context, packetId, payload);
            case PLAY -> handlePlay(context, packetId, payload);
        }
    }

    private void handleHandshake(PlayerContext context, int packetId, Buffer payload) {
    }

    private void handleLogin(PlayerContext context, int packetId, Buffer payload) {
    }

    private void handlePlay(PlayerContext context, int packetId, Buffer payload) {
    }
}
