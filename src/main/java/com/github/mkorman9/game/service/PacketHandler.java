package com.github.mkorman9.game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.HandshakePacket;
import io.vertx.core.buffer.Buffer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class PacketHandler {
    @Inject
    ObjectMapper objectMapper;

    @Inject
    HandshakeHandler handshakeHandler;

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
        if (packetId == HandshakePacket.ID) {
            handshakeHandler.onHandshake(context, readPayload(payload, HandshakePacket.class));
        }
    }

    private void handleLogin(PlayerContext context, int packetId, Buffer payload) {
    }

    private void handlePlay(PlayerContext context, int packetId, Buffer payload) {
    }

    private <T> T readPayload(Buffer payload, Class<T> clazz) {
        try {
            return objectMapper.readValue(payload.getBytes(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
