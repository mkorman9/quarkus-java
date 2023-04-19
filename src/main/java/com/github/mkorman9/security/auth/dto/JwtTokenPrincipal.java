package com.github.mkorman9.security.auth.dto;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.Principal;
import java.util.UUID;

public record JwtTokenPrincipal(DecodedJWT token) implements Principal {
    public static final String NAME_CLAIM = "name";
    public static final String ROLES_CLAIM = "roles";

    @Override
    public String getName() {
        return token.getClaim(NAME_CLAIM).asString();
    }

    public UUID getUserId() {
        return UUID.fromString(token.getSubject());
    }
}
