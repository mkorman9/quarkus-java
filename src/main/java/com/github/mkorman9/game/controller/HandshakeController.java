package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.handshake.HandshakePacket;
import com.github.mkorman9.game.dto.packet.handshake.HandshakeResponsePacket;
import com.github.mkorman9.game.service.PacketSender;
import com.github.mkorman9.game.service.PlayerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HandshakeController {
    private static final Logger LOG = LoggerFactory.getLogger(HandshakeController.class);

    @Inject
    PacketSender sender;

    @Inject
    PlayerRegistry playerRegistry;

    public void onHandshake(PlayerContext context, HandshakePacket packet) {
        LOG.info(
                "Connected from: {}, device: {}, client version: {}",
                context.getSocket().remoteAddress().hostAddress(),
                packet.getDevice(),
                packet.getClientVersion()
        );

        context.setState(ConnectionState.LOGIN);
        sender.send(context, new HandshakeResponsePacket(playerRegistry.getInPlayCount(), "1.0.0"));
    }
}
