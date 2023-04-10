package com.github.mkorman9.security.auth.service;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.mkorman9.security.auth.model.User;
import io.quarkus.runtime.ExecutorRecorder;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TokenAuthenticationService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationService.class);

    private final JWTVerifier verifier;
    private final UserService userService;

    @Inject
    public TokenAuthenticationService(JWTHelper jwt, UserService userService) {
        this.verifier = jwt.getVerification().build();
        this.userService = userService;
    }

    public Optional<SecurityContext> authenticate(String token) {
        var maybeUserId = verifyTokenAndGetUserId(token);
        if (maybeUserId.isEmpty()) {
            return Optional.empty();
        }

        var userId = maybeUserId.get();

        return getUserById(userId)
                .map(this::createSecurityContext);
    }

    public Uni<SecurityContext> authenticateAsync(String token) {
        var maybeUserId = verifyTokenAndGetUserId(token);
        if (maybeUserId.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException());
        }

        var userId = maybeUserId.get();

        return getUserByIdAsync(userId)
                .map(this::createSecurityContext);
    }

    private Optional<UUID> verifyTokenAndGetUserId(String token) {
        try {
            var decodedToken = verifier.verify(token);
            var uid = decodedToken.getClaim("uid").asString();

            return Optional.ofNullable(UUID.fromString(uid));
        } catch (JWTVerificationException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<User> getUserById(UUID userId) {
        try {
            return userService.getById(userId);
        } catch (Exception e) {
            LOG.error("Error while authenticating user", e);
            return Optional.empty();
        }
    }

    private Uni<User> getUserByIdAsync(UUID userId) {
        return Uni.createFrom().deferred(() ->
                Uni.createFrom().emitter(consumer ->
                        ExecutorRecorder.getCurrent().execute(() ->
                                getUserById(userId).ifPresentOrElse(
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
