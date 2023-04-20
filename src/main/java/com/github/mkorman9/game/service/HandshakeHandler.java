package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.handshake.HandshakePacket;
import com.github.mkorman9.game.dto.packet.handshake.HandshakeResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HandshakeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HandshakeHandler.class);

    @Inject
    PacketSender sender;

    public void onHandshake(PlayerContext context, HandshakePacket packet) {
        LOG.info(
                "Connected from: {}, device: {}, client version: {}",
                context.getSocket().remoteAddress().hostAddress(),
                packet.getDevice(),
                packet.getClientVersion()
        );

        sender.send(context, new HandshakeResponsePacket(1, "1.0.0"));
        context.setState(ConnectionState.LOGIN);
    }
}
