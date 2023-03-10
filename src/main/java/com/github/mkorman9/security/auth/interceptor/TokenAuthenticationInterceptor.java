package com.github.mkorman9.security.auth.interceptor;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.mkorman9.security.auth.service.JWTHelper;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;
import java.util.Optional;

public class TokenAuthenticationInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    private final JWTVerifier verifier;
    private final UserAuthenticator userAuthenticator;

    @Inject
    public TokenAuthenticationInterceptor(JWTHelper jwt, UserAuthenticator userAuthenticator) {
        this.verifier = jwt.getVerification().build();
        this.userAuthenticator = userAuthenticator;
    }

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        var maybeToken = extractToken(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        try {
            var decodedToken = verifier.verify(maybeToken.get());
            var uid = decodedToken.getClaim("uid").asString();

            if (uid == null) {
                return Uni.createFrom().voidItem();
            }

            return userAuthenticator.authenticate(uid)
                    .map((securityContext) -> {
                        context.setSecurityContext(securityContext);
                        return null;
                    })
                    .replaceWithVoid()
                    .onFailure().recoverWithNull().replaceWithVoid();
        } catch (JWTVerificationException e) {
            return Uni.createFrom().voidItem();
        }
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
