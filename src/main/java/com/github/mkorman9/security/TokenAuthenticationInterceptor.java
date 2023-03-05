package com.github.mkorman9.security;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;
import java.util.Optional;

public class TokenAuthenticationInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "bearer";

    @Inject
    TokenAuthenticationMethod tokenAuthenticationMethod;

    @ServerRequestFilter(preMatching = true)
    public Uni<Void> intercept(ContainerRequestContext context) {
        var maybeToken = extractToken(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        return tokenAuthenticationMethod.authenticate(maybeToken.get()).
                map(securityContext -> {
                    context.setSecurityContext(securityContext);
                    return null;
                })
                .onFailure().recoverWithNull().replaceWithVoid();
    }

    private Optional<String> extractToken(ContainerRequestContext context) {
        List<String> authHeaderValues = context.
                getHeaders().
                get(AUTHORIZATION_HEADER);
        if (authHeaderValues == null || authHeaderValues.isEmpty()) {
            return Optional.empty();
        }

        var headerValue = authHeaderValues.get(0);
        var headerParts = headerValue.split("\\s+");

        if (headerParts.length != 2 || !headerParts[0].equalsIgnoreCase(TOKEN_TYPE)) {
            return Optional.empty();
        }

        var token = headerParts[1];
        return Optional.of(token);
    }
}
