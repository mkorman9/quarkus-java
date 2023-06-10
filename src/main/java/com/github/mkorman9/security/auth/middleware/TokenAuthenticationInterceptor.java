package com.github.mkorman9.security.auth.middleware;

import com.github.mkorman9.security.auth.model.Token;
import com.github.mkorman9.security.auth.service.TokenService;
import io.quarkus.runtime.ExecutorRecorder;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import java.security.Principal;
import java.util.Optional;

@ApplicationScoped
public class TokenAuthenticationInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Inject
    TokenService tokenService;

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        var maybeToken = extractToken(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        var token = maybeToken.get();

        return Uni.createFrom().emitter(consumer -> {
            ExecutorRecorder.getCurrent().execute(() -> {
                try {
                    tokenService.verifyToken(token)
                            .ifPresent(t -> context.setSecurityContext(createSecurityContext(t)));
                    consumer.complete(null);
                } catch (Exception e) {
                    consumer.fail(e);
                }
            });
        });
    }

    public static Optional<String> extractToken(ContainerRequestContext context) {
        var headerValue = context
                .getHeaders()
                .getFirst(AUTHORIZATION_HEADER);
        if (headerValue == null) {
            return Optional.empty();
        }

        var headerParts = headerValue.split("\\s+");
        if (headerParts.length != 2 || !headerParts[0].equalsIgnoreCase(TOKEN_TYPE)) {
            return Optional.empty();
        }

        return Optional.of(headerParts[1]);
    }

    private SecurityContext createSecurityContext(Token token) {
        var user = token.getUser();
        var roles = user.getRolesSet();

        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return user;
            }

            @Override
            public boolean isUserInRole(String role) {
                return roles.contains(role);
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return SecurityContext.DIGEST_AUTH;
            }
        };
    }
}
