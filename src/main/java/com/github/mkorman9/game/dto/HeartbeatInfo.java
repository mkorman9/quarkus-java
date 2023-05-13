package com.github.mkorman9.game.dto;

import lombok.Data;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class HeartbeatInfo {
    private AtomicReference<Instant> lastResponse = new AtomicReference<>(Instant.now());

    private AtomicReference<Instant> lastSent = new AtomicReference<>(Instant.now());

    private AtomicLong ping = new AtomicLong(0);

    private AtomicLong lastData = new AtomicLong(0);
}
