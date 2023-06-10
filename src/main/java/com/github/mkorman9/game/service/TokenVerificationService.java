package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.TokenVerificationRequest;
import com.github.mkorman9.game.dto.TokenVerificationResponse;
import com.github.mkorman9.security.auth.service.TokenService;
import com.github.mkorman9.security.auth.service.UserService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TokenVerificationService {
    private static final String CHANNEL = "token-verification";

    @Inject
    TokenService tokenService;

    @Inject
    UserService userService;

    @Inject
    EventBus eventBus;

    public Future<TokenVerificationResponse> verifyToken(TokenVerificationRequest request) {
        return eventBus.<TokenVerificationResponse>request(CHANNEL, request)
                .map(Message::body);
    }

    @ConsumeEvent(CHANNEL)
    @Blocking
    TokenVerificationResponse onTokenVerificationRequest(TokenVerificationRequest request) {
        var maybeToken = tokenService.verifyToken(request.token());
        if (maybeToken.isEmpty()) {
            return TokenVerificationResponse.builder()
                    .verified(false)
                    .build();
        }

        var user = maybeToken.get().getUser();

        return TokenVerificationResponse.builder()
                .verified(true)
                .userId(user.getId())
                .userName(user.getName())
                .roles(user.getRolesSet())
                .build();
    }
}
