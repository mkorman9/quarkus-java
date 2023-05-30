package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.handshake.HandshakePacket;
import com.github.mkorman9.game.dto.packet.handshake.HandshakeResponsePacket;
import com.github.mkorman9.game.service.PacketSender;
import com.github.mkorman9.game.service.PlayerRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Slf4j
public class HandshakeController {
    @Inject
    PacketSender sender;

    @Inject
    PlayerRegistry playerRegistry;

    public void onHandshake(PlayerContext context, HandshakePacket packet) {
        log.info(
                "Received handshake from: {}, device: {}, client version: {}",
                context.getSocket().remoteAddress().hostAddress(),
                packet.getDevice(),
                packet.getClientVersion()
        );

        context.setState(ConnectionState.LOGIN);
        sender.send(context, new HandshakeResponsePacket(playerRegistry.countInPlay(), "1.0.0"));
    }
}
