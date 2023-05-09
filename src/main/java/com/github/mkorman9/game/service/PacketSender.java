package com.github.mkorman9.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.VarInt;
import com.github.mkorman9.game.dto.packet.Response;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PacketSender {
    public static final int PACKET_ID_LENGTH = 2;

    @Inject
    ObjectMapper objectMapper;

    public <T extends Response> Future<Void> send(PlayerContext context, T obj) {
        try {
            var payload = objectMapper.writeValueAsBytes(obj);
            var packet = Buffer.buffer()
                    .appendBytes(VarInt.encode(payload.length + PACKET_ID_LENGTH))
                    .appendShort(obj.packetId())
                    .appendBytes(payload);

            return context.getSocket().write(packet);
        } catch (JsonProcessingException e) {
            return Future.failedFuture(e);
        }
    }
}
