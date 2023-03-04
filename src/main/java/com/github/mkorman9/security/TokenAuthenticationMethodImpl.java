package com.github.mkorman9.security;

import com.github.mkorman9.models.User;
import com.github.mkorman9.services.UserService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.UniHelper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TokenAuthenticationMethodImpl implements TokenAuthenticationMethod {
    @Inject
    UserService userService;

    @Inject
    Vertx vertx;

    @Override
    public Uni<Optional<SecurityContext>> authenticate(String token) {
        var maybeUserId = convertTokenToUUID(token);
        if (maybeUserId.isEmpty()) {
            return Uni.createFrom().item(Optional::empty);
        }
        UUID userId = maybeUserId.get();

        Future<User> userFuture = vertx.executeBlocking((promise) -> {
            var maybeUser = userService.getById(userId);
            maybeUser.ifPresentOrElse(promise::complete, () -> promise.fail(""));
        });
        Uni<User> userUni = UniHelper.toUni(userFuture);

        return userUni.
                onItem().transform((user) -> Optional.of(createSecurityContext(user))).
                onFailure().recoverWithItem(Optional::empty);
    }

    private SecurityContext createSecurityContext(User user) {
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return user;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
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
