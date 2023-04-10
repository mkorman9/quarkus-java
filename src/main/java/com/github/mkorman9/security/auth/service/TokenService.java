package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.model.Token;
import com.github.mkorman9.security.auth.model.User;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class TokenService {
    private static final String TOKEN_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int TOKEN_LENGTH = 48;

    @Inject
    EntityManager entityManager;

    private final SecureRandom random;

    @SneakyThrows
    public TokenService() {
        random = SecureRandom.getInstanceStrong();
    }

    @Transactional
    public Optional<Token> findToken(String token) {
        try {
            var result = entityManager
                    .createQuery("from Token t where t.token = :token and t.isValid = true", Token.class)
                    .setParameter("token", token)
                    .getSingleResult();

            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Token issueToken(User owner) {
        var token = new Token();
        token.setToken(generateToken());
        token.setUser(owner);
        token.setIssuedAt(Instant.now());
        token.setValid(true);

        entityManager.persist(token);

        return token;
    }

    private String generateToken() {
        return random.ints(TOKEN_LENGTH, 0, TOKEN_CHARSET.length())
                .mapToObj(TOKEN_CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
