package com.github.mkorman9.security.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.mkorman9.security.auth.dto.JwtTokenPrincipal;
import com.github.mkorman9.security.auth.model.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    public Optional<JwtTokenPrincipal> verifyToken(String token) {
        try {
            var principal = new JwtTokenPrincipal(verifier.verify(token));
            return Optional.of(principal);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String issueToken(User owner) {
        var now = Instant.now();

        return JWT.create()
                .withAudience(AUDIENCE)
                .withSubject(owner.getId().toString())
                .withClaim(JwtTokenPrincipal.NAME_CLAIM, owner.getName())
                .withClaim(JwtTokenPrincipal.ROLES_CLAIM, owner.getRolesSet().stream().toList())
                .withIssuedAt(now)
                .withExpiresAt(now.plus(TOKEN_DURATION))
                .sign(algorithm);
    }
}
