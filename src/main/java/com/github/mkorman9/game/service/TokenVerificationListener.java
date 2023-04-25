package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.TokenVerificationRequest;
import com.github.mkorman9.game.dto.TokenVerificationResponse;
import com.github.mkorman9.security.auth.service.TokenService;
import com.github.mkorman9.security.auth.service.UserService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TokenVerificationListener {
    @Inject
    TokenService tokenService;

    @Inject
    UserService userService;

    @ConsumeEvent(TokenVerificationRequest.NAME)
    @Blocking
    public TokenVerificationResponse onTokenVerificationRequest(TokenVerificationRequest request) {
        var maybeToken = tokenService.verifyToken(request.token());
        if (maybeToken.isEmpty()) {
            return new TokenVerificationResponse(false, null, null, null);
        }

        var token = maybeToken.get();

        var maybeUser = userService.getById(token.getUserId());
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("Owner of valid token cannot be found");
        }

        var user = maybeUser.get();
        return new TokenVerificationResponse(true, user.getId(), user.getName(), user.getRolesSet());
    }
}
