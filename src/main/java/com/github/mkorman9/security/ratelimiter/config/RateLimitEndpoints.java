package com.github.mkorman9.security.ratelimiter.config;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class RateLimitEndpoints {
    public List<RateLimitConfig> getEndpointsConfiguration() {
        return List.of(
                new RateLimitConfig("/limited/path", 25, Duration.ofMinutes(1))
        );
    }
}
