package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.dto.TokenIssueRequest;
import com.github.mkorman9.security.auth.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class TokenService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);
    private static final String TOKEN_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int TOKEN_LENGTH = 48;
    private static final SecureRandom RANDOM = selectSecureRandom();

    @Inject
    EntityManager entityManager;

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
    public Token issueToken(TokenIssueRequest request) {
        var token = new Token();
        token.setToken(generateToken());
        token.setUser(request.getUser());
        token.setIssuedAt(Instant.now());
        token.setRemoteAddress(request.getRemoteAddress());
        token.setDevice(request.getDevice());
        token.setValid(true);

        entityManager.persist(token);

        return token;
    }

    private String generateToken() {
        return RANDOM.ints(TOKEN_LENGTH, 0, TOKEN_CHARSET.length())
                .mapToObj(TOKEN_CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private static SecureRandom selectSecureRandom() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                return SecureRandom.getInstanceStrong();
            } else {
                return SecureRandom.getInstance("NativePRNGNonBlocking");
            }
        } catch (NoSuchAlgorithmException e) {
            LOG.error("SecureRandom algorithm cannot be selected", e);
            throw new RuntimeException(e);
        }
    }
}
