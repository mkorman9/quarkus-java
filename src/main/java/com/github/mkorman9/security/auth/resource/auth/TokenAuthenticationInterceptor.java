package com.github.mkorman9.security.auth.resource.auth;

import com.auth0.jwt.JWTVerifier;
import com.github.mkorman9.security.auth.service.JWTHelper;
import com.github.mkorman9.security.auth.service.TokenAuthenticationService;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;

public class TokenAuthenticationInterceptor {
    private final JWTVerifier verifier;
    private final TokenAuthenticationService tokenAuthenticationService;

    @Inject
    public TokenAuthenticationInterceptor(JWTHelper jwt, TokenAuthenticationService tokenAuthenticationService) {
        this.verifier = jwt.getVerification().build();
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        var maybeToken = BearerTokenExtractor.extract(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        return tokenAuthenticationService.authenticateAsync(maybeToken.get())
                .map(securityContext -> {
                    context.setSecurityContext(securityContext);
                    return null;
                })
                .replaceWithVoid()
                .onFailure().recoverWithNull().replaceWithVoid();
    }
}
