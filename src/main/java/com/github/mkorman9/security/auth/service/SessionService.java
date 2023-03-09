package com.github.mkorman9.security.auth.service;

import com.auth0.jwt.JWT;
import com.github.mkorman9.security.auth.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class SessionService {
    @Inject
    JWTHelper jwt;

    public String newToken(User user) {
        var token = JWT.create()
                .withClaim("uid", user.getId().toString())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(Duration.ofHours(1)));

        return jwt.sign(token);
    }
}
