package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.dto.TokenDto;
import com.github.mkorman9.security.auth.dto.TokenIssueRequest;
import com.github.mkorman9.security.auth.dto.converter.TokenDtoConverter;
import com.github.mkorman9.security.auth.model.Token;
import com.github.mkorman9.security.auth.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class TokenService {
    private static final String TOKEN_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int TOKEN_LENGTH = 64;
    private static final SecureRandom RANDOM = selectSecureRandom();

    @Inject
    EntityManager entityManager;

    @Inject
    TokenDtoConverter tokenDtoConverter;

    @Transactional
    public Optional<TokenDto> verifyToken(String token) {
        try {
            var result = entityManager
                    .createQuery("from Token t where t.token = :token and t.isValid = true", Token.class)
                    .setParameter("token", token)
                    .getSingleResult();

            return Optional.of(tokenDtoConverter.convertToDto(result));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<TokenDto> issueToken(TokenIssueRequest request) {
        var user = entityManager.find(User.class, request.getUserId());
        if (user == null) {
            return Optional.empty();
        }

        var token = new Token();
        token.setToken(generateToken());
        token.setUser(user);
        token.setIssuedAt(Instant.now());
        token.setRemoteAddress(request.getRemoteAddress());
        token.setDevice(request.getDevice());
        token.setValid(true);

        entityManager.persist(token);
        return Optional.of(tokenDtoConverter.convertToDto(token));
    }

    private String generateToken() {
        return RANDOM.ints(TOKEN_LENGTH, 0, TOKEN_CHARSET.length())
                .mapToObj(TOKEN_CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    @SneakyThrows
    private static SecureRandom selectSecureRandom() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return SecureRandom.getInstanceStrong();
        } else {
            return SecureRandom.getInstance("NativePRNGNonBlocking");
        }
    }
}
