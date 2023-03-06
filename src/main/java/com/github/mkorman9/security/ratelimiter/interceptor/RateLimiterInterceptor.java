package com.github.mkorman9.security.ratelimiter.interceptor;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;

public class RateLimiterInterceptor {
    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHENTICATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        // TODO

        return Uni.createFrom().voidItem();
    }
}
