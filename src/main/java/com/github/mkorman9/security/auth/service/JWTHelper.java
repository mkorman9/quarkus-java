package com.github.mkorman9.security.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JWTHelper {
    private static final Logger LOG = LoggerFactory.getLogger(JWTHelper.class);

    private final Algorithm algorithm;

    @Inject
    public JWTHelper(
            @ConfigProperty(name="jwt.secret") String secret
    ) {
        this.algorithm = Algorithm.HMAC512(secret);
    }

    public Verification getVerification() {
        return JWT.require(algorithm);
    }

    public String sign(JWTCreator.Builder builder) {
        return builder.sign(algorithm);
    }
}
