package com.github.mkorman9.game.job;

import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.PlayerDisconnectReason;
import com.github.mkorman9.game.dto.packet.play.HeartbeatRequest;
import com.github.mkorman9.game.server.TcpServerConfig;
import com.github.mkorman9.game.service.PacketSender;
import com.github.mkorman9.game.service.PlayerRegistry;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@ApplicationScoped
public class HeartbeatJob {
    private static final Random RANDOM = new Random();

    @Inject
    PlayerRegistry playerRegistry;

    @Inject
    PacketSender packetSender;

    private final Duration heartbeatTimeout;

    @Inject
    public HeartbeatJob(TcpServerConfig config) {
        this.heartbeatTimeout = Duration.parse("PT" + config.heartbeatTimeout());
    }

    @Scheduled(every = "${tcp.server.heartbeat-interval}")
    public void sendHeartbeats() {
        playerRegistry.forEachInPlay(context -> {
            if (shouldBeDisconnected(context)) {
                context.disconnect(PlayerDisconnectReason.TIMEOUT);
                return;
            }

            var heartbeatData = RANDOM.nextLong();
            var request = new HeartbeatRequest(heartbeatData);
            context.getHeartbeatInfo().getLastData().set(heartbeatData);

            packetSender.send(context, request)
                    .onSuccess(v -> successfulSend(context))
                    .onFailure(t -> context.disconnect(PlayerDisconnectReason.PEER_RESET));
        });
    }

    private boolean shouldBeDisconnected(PlayerContext context) {
        var lastResponse = context.getHeartbeatInfo().getLastResponse().get();
        var deadline = Instant.now().minus(heartbeatTimeout);

        return lastResponse.isBefore(deadline);
    }

    private void successfulSend(PlayerContext context) {
        context.getHeartbeatInfo().getLastSent().set(Instant.now());
    }
}
