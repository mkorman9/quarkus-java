package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.UserDataRequest;
import com.github.mkorman9.game.dto.UserDataResponse;
import com.github.mkorman9.game.dto.packet.login.LoginFailedResponsePacket;
import com.github.mkorman9.game.dto.packet.login.LoginPacket;
import com.github.mkorman9.game.dto.packet.login.LoginSuccessResponsePacket;
import com.github.mkorman9.game.service.PacketSender;
import com.github.mkorman9.security.auth.service.TokenService;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Inject
    PacketSender sender;

    @Inject
    TokenService tokenService;

    @Inject
    EventBus eventBus;

    public void onLogin(PlayerContext context, LoginPacket packet) {
        var maybeToken = tokenService.verifyToken(packet.getToken());
        if (maybeToken.isEmpty()) {
            LOG.info("{} login failed", context.getSocket().remoteAddress().hostAddress());
            sender.send(context, new LoginFailedResponsePacket("Login Failed"))
                    .onSuccess(v -> context.getSocket().close());
            return;
        }

        var token = maybeToken.get();

        eventBus.<UserDataResponse>request(UserDataRequest.NAME, new UserDataRequest(token.getUserId()))
                .onSuccess(m -> {
                    LOG.info(
                            "{} logged in as {}",
                            context.getSocket().remoteAddress().hostAddress(),
                            token.getName()
                    );

                    context.setUserName(token.getName());
                    context.setUserId(token.getUserId());
                    context.setState(ConnectionState.PLAY);

                    sender.send(context, new LoginSuccessResponsePacket(Instant.now(), m.body().roles()));
                })
                .onFailure(t -> {
                    LOG.error("Error while requesting user data", t);
                    context.getSocket().close();
                });
    }
}
