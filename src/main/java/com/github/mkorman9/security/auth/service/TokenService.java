package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.model.Token;
import com.github.mkorman9.security.auth.model.User;
import io.smallrye.mutiny.Uni;
import lombok.SneakyThrows;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.SecureRandom;
import java.time.Instant;

@ApplicationScoped
public class TokenService {
    private static final String TOKEN_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int TOKEN_LENGTH = 48;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    private final SecureRandom random;

    @SneakyThrows
    public TokenService() {
        random = SecureRandom.getInstanceStrong();
    }

    public Uni<Token> findToken(String token) {
        return sessionFactory.withTransaction(session -> {
            return session
                    .createQuery("from Token t where t.token = :token and t.isValid = true", Token.class)
                    .setParameter("token", token)
                    .getSingleResultOrNull();
        });
    }

    public Uni<Token> issueToken(User owner) {
        var token = new Token();
        token.setToken(generateToken());
        token.setUser(owner);
        token.setIssuedAt(Instant.now());
        token.setValid(true);

        return sessionFactory
                .withTransaction(session -> {
                    return session.persist(token);
                })
                .map(v -> token);
    }

    private String generateToken() {
        return random.ints(TOKEN_LENGTH, 0, TOKEN_CHARSET.length())
                .mapToObj(TOKEN_CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
