package com.github.mkorman9.security.ratelimiter.interceptor;

import com.github.mkorman9.security.ratelimiter.config.RateLimitConfig;
import com.github.mkorman9.security.ratelimiter.config.RateLimitEndpoints;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;

public class RateLimiterInterceptor {
    private final List<RateLimitConfig> endpointsConfig;

    @Inject
    public RateLimiterInterceptor(RateLimitEndpoints endpoints) {
        this.endpointsConfig = endpoints.getEndpointsConfiguration();
    }

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHENTICATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        var path = context.getUriInfo().getPath();
        var maybeEndpointConfig = endpointsConfig.stream()
                .filter(config -> path.startsWith(config.endpointPrefix()))
                .findFirst();
        if (maybeEndpointConfig.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        var endpointConfig = maybeEndpointConfig.get();

        // TODO

        return Uni.createFrom().voidItem();
    }
}
