package com.github.mkorman9.security;

import com.github.mkorman9.models.User;
import com.github.mkorman9.services.UserService;
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
public class TokenAuthenticationMethodImpl implements TokenAuthenticationMethod {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationMethodImpl.class);

    @Inject
    UserService userService;

    @Override
    public Uni<SecurityContext> authenticate(String token) {
        var maybeUserId = convertTokenToUUID(token);
        if (maybeUserId.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException());
        }
        UUID userId = maybeUserId.get();

        Uni<User> userUni = Uni.createFrom().deferred(() ->
            Uni.createFrom().emitter(uniEmitter ->
                    ExecutorRecorder.getCurrent().execute(() ->
                        resolveUser(userId).ifPresentOrElse(
                                uniEmitter::complete,
                                () -> uniEmitter.fail(new IllegalArgumentException())
                        )
                    )
            )
        );

        return userUni.onItem().transform(this::createSecurityContext);
    }

    private Optional<User> resolveUser(UUID userId) {
        try {
            return userService.getById(userId);
        } catch (Exception e) {
            LOG.error("Error while authenticating security token", e);
            return Optional.empty();
        }
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

    private static Optional<UUID> convertTokenToUUID(String token) {
        try {
            return Optional.of(UUID.fromString(token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
