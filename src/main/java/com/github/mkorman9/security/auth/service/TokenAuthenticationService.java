package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.model.Token;
import com.github.mkorman9.security.auth.model.User;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@ApplicationScoped
public class TokenAuthenticationService {
    @Inject
    TokenService tokenService;

    public Uni<SecurityContext> authenticate(String token) {
        return tokenService.findToken(token)
                .map(Token::getUser)
                .map(this::createSecurityContext);
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
