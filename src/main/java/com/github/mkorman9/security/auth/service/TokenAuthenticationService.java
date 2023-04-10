package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.model.Token;
import com.github.mkorman9.security.auth.model.User;
import io.quarkus.runtime.ExecutorRecorder;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;

@ApplicationScoped
public class TokenAuthenticationService {
    @Inject
    TokenService tokenService;

    public Optional<SecurityContext> authenticate(String token) {
        var maybeToken = tokenService.findToken(token);
        if (maybeToken.isEmpty()) {
            return Optional.empty();
        }

        var user = maybeToken.get().getUser();
        return Optional.of(createSecurityContext(user));
    }

    public Uni<SecurityContext> authenticateAsync(String token) {
        return findTokenAsync(token)
                .map(Token::getUser)
                .map(this::createSecurityContext);
    }

    private Uni<Token> findTokenAsync(String token) {
        return Uni.createFrom().deferred(() ->
                Uni.createFrom().emitter(consumer ->
                        ExecutorRecorder.getCurrent().execute(() ->
                                tokenService.findToken(token).ifPresentOrElse(
                                        consumer::complete,
                                        () -> consumer.fail(new IllegalArgumentException())
                                )
                        )
                )
        );
    }

    private SecurityContext createSecurityContext(User user) {
        var userRoles = user.getRolesSet();

        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return user;
            }

            @Override
            public boolean isUserInRole(String role) {
                return userRoles.contains(role);
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
