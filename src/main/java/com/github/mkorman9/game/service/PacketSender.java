package com.github.mkorman9.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkorman9.game.dto.PlayerContext;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PacketSender {
    @Inject
    ObjectMapper objectMapper;

    public Future<Void> send(PlayerContext context, int packetId, Object obj) {
        try {
            var payload = objectMapper.writeValueAsString(obj);
            var packet = Buffer.buffer()
                    .appendInt(payload.length() + 4)
                    .appendInt(packetId)
                    .appendString(payload);

            return context.getSocket().write(packet);
        } catch (JsonProcessingException e) {
            return Future.failedFuture(e);
        }
    }
}
