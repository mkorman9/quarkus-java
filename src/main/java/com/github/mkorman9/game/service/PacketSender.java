package com.github.mkorman9.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.VarInt;
import com.github.mkorman9.game.dto.packet.Sendable;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PacketSender {
    @Inject
    ObjectMapper objectMapper;

    public <T extends Sendable> Future<Void> send(PlayerContext context, T obj) {
        try {
            var payload = objectMapper.writeValueAsBytes(obj);
            var packetId = VarInt.of(obj.packetId());
            var packetLength = VarInt.of(payload.length + packetId.getLength());
            var packet = Buffer.buffer()
                    .appendBytes(packetLength.encode())
                    .appendBytes(packetId.encode())
                    .appendBytes(payload);

            return context.getSocket().write(packet);
        } catch (JsonProcessingException e) {
            return Future.failedFuture(e);
        }
    }
}
