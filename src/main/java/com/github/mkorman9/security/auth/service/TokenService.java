package com.github.mkorman9.security.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.mkorman9.security.auth.model.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class TokenService {
    private static final String AUDIENCE = "quarkus-java";
    private static final Duration TOKEN_DURATION = Duration.ofHours(1);

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    @Inject
    public TokenService(
            @ConfigProperty(name="jwt.secret") String secret
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm)
                .withAudience(AUDIENCE)
                .build();
    }

    public Optional<DecodedJWT> validateToken(String token) {
        try {
            return Optional.of(verifier.verify(token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String issueToken(User owner) {
        var now = Instant.now();

        return JWT.create()
                .withAudience(AUDIENCE)
                .withSubject(owner.getId().toString())
                .withClaim("name", owner.getName())
                .withClaim("roles", owner.getRolesSet().stream().toList())
                .withIssuedAt(now)
                .withExpiresAt(now.plus(TOKEN_DURATION))
                .sign(algorithm);
    }
}
