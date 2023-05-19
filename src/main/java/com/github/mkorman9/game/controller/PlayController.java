package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.play.HeartbeatResponse;
import com.github.mkorman9.game.service.PacketSender;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
@Slf4j
public class PlayController {
    @Inject
    PacketSender sender;

    public void onHeartbeatResponse(PlayerContext context, HeartbeatResponse response) {
        var lastData = context.getHeartbeatInfo().getLastData().get();
        if (lastData != response.getData()) {
            return;
        }

        var lastResponse = Instant.now();
        var lastSent = context.getHeartbeatInfo().getLastSent().get();

        context.getHeartbeatInfo().getLastResponse().set(lastResponse);
        context.getHeartbeatInfo().getPing().set(Duration.between(lastSent, lastResponse).toMillis());
    }
}
