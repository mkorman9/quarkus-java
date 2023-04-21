package com.github.mkorman9.game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkorman9.game.controller.HandshakeController;
import com.github.mkorman9.game.controller.LoginController;
import com.github.mkorman9.game.controller.PlayController;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.handshake.HandshakePacket;
import com.github.mkorman9.game.dto.packet.login.LoginPacket;
import io.vertx.core.buffer.Buffer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class PacketDispatcher {
    @Inject
    ObjectMapper objectMapper;

    @Inject
    HandshakeController handshakeController;

    @Inject
    LoginController loginController;

    @Inject
    PlayController playController;

    public void dispatch(PlayerContext context, Buffer packet) {
        var packetId = 0;
        var payload = packet;

        try {
            packetId = packet.getInt(0);
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
            handshakeController.onHandshake(context, readPayload(payload, HandshakePacket.class));
        }
    }

    private void handleLogin(PlayerContext context, int packetId, Buffer payload) {
        if (packetId == LoginPacket.ID) {
            loginController.onLogin(context, readPayload(payload, LoginPacket.class));
        }
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
