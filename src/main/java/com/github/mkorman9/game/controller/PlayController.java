package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.play.HeartbeatResponse;
import com.github.mkorman9.game.service.PacketSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class PlayController {
    private static final Logger LOG = LoggerFactory.getLogger(PlayController.class);

    @Inject
    PacketSender sender;

    public void onHeartbeatResponse(PlayerContext context, HeartbeatResponse response) {
        var lastResponse = Instant.now();
        var lastSent = context.getHeartbeatInfo().getLastSent().get();

        context.getHeartbeatInfo().getLastResponse().set(lastResponse);
        context.getHeartbeatInfo().getPing().set(Duration.between(lastSent, lastResponse).toMillis());
    }
}
