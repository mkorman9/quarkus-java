package com.github.mkorman9.game.job;

import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HeartbeatJob {
    @Scheduled(every = "10s")
    public void sendHeartbeats() {
    }
}
