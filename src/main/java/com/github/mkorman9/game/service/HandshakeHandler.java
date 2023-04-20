package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.HandshakePacket;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HandshakeHandler {
    public void onHandshake(PlayerContext context, HandshakePacket packet) {
    }
}
