package com.github.mkorman9.game.job;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.PlayerDisconnectReason;
import com.github.mkorman9.game.dto.packet.play.HeartbeatRequest;
import com.github.mkorman9.game.server.TcpServerConfig;
import com.github.mkorman9.game.service.PacketSender;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class HeartbeatJob {
    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatJob.class);

    @Inject
    PlayerRegistry playerRegistry;

    @Inject
    PacketSender packetSender;

    @Inject
    TcpServerConfig config;

    @Scheduled(every = "${tcp.server.heartbeat-interval}s")
    public void sendHeartbeats() {
        playerRegistry.forEachInPlay(context -> {
            if (shouldBeDisconnected(context)) {
                context.disconnect(PlayerDisconnectReason.TIMEOUT);
                return;
            }

            var request = new HeartbeatRequest(1000);
            packetSender.send(context, request)
                    .onSuccess(v -> successfulSend(context))
                    .onFailure(t -> failedSend(context, t));
        });
    }

    private boolean shouldBeDisconnected(PlayerContext context) {
        var lastResponse = context.getHeartbeatInfo().getLastResponse().get();
        var deadline = Instant.now().minus(Duration.ofSeconds(config.heartbeatTimeout()));

        return lastResponse.isBefore(deadline);
    }

    private void successfulSend(PlayerContext context) {
        context.getHeartbeatInfo().getLastSent().set(Instant.now());
    }

    private static void failedSend(PlayerContext context, Throwable t) {
        LOG.error("Heartbeat request failed for player {}", context.getUserId(), t);
        context.disconnect(PlayerDisconnectReason.TIMEOUT);
    }
}
