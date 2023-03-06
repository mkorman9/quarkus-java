package com.github.mkorman9.security.ratelimiter.config;

import java.time.Duration;

public record RateLimitConfig(
        String endpointPrefix,
        int maxRequests,
        Duration timeWindow
) {
}
