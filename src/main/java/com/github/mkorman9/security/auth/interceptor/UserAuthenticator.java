package com.github.mkorman9.security.auth.interceptor;

import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticator.class);

    @Inject
    UserService userService;

    public Optional<SecurityContext> authenticate(String uid) {
        try {
            var userId = UUID.fromString(uid);
            return resolveUser(userId)
                    .map(this::createSecurityContext);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<User> resolveUser(UUID userId) {
        try {
            return userService.getById(userId);
        } catch (Exception e) {
            LOG.error("Error while authenticating user", e);
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
}
