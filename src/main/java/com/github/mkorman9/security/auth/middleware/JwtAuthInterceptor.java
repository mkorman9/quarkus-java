package com.github.mkorman9.security.auth.middleware;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.mkorman9.security.auth.dto.JwtTokenPrincipal;
import com.github.mkorman9.security.auth.service.TokenService;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;

@ApplicationScoped
public class JwtAuthInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Inject
    TokenService tokenService;

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        return Uni.createFrom().deferred(() ->
                Uni.createFrom().emitter(consumer -> {
                    extractToken(context)
                            .flatMap(token -> tokenService.validateToken(token))
                            .ifPresent(decoded -> context.setSecurityContext(createSecurityContext(decoded)));
                    consumer.complete(null);
                })
        );
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

    private SecurityContext createSecurityContext(DecodedJWT decodedToken) {
        var principal = new JwtTokenPrincipal(decodedToken);
        var rolesRaw = decodedToken.getClaim(JwtTokenPrincipal.ROLES_CLAIM).asList(String.class);
        var roles = rolesRaw != null ? new HashSet<>(rolesRaw) : new HashSet<>();

        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return principal;
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
