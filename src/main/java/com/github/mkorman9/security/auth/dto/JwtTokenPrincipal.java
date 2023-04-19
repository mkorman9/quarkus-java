package com.github.mkorman9.security.auth.dto;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.Principal;

public record JwtTokenPrincipal(DecodedJWT token) implements Principal {
    @Override
    public String getName() {
        return token.getSubject();
    }
}
