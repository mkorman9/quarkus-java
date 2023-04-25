package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.TokenVerificationRequest;
import com.github.mkorman9.game.dto.TokenVerificationResponse;
import com.github.mkorman9.game.dto.packet.login.LoginFailedResponsePacket;
import com.github.mkorman9.game.dto.packet.login.LoginPacket;
import com.github.mkorman9.game.dto.packet.login.LoginSuccessResponsePacket;
import com.github.mkorman9.game.service.PacketSender;
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
    EventBus eventBus;

    public void onLogin(PlayerContext context, LoginPacket packet) {
        var request = new TokenVerificationRequest(packet.getToken());
        eventBus.<TokenVerificationResponse>request(TokenVerificationRequest.NAME, request)
                .onSuccess(m -> performLogin(context, m.body()))
                .onFailure(t -> {
                    LOG.error("Error while verifying token", t);
                    context.getSocket().close();
                });
    }

    private void performLogin(PlayerContext context, TokenVerificationResponse response) {
        if (!response.verified()) {
            LOG.info("{} login failed", context.getSocket().remoteAddress().hostAddress());

            sender.send(context, new LoginFailedResponsePacket("Login Failed"))
                    .onSuccess(v -> context.getSocket().close());

            return;
        }

        LOG.info(
                "{} logged in as {}",
                context.getSocket().remoteAddress().hostAddress(),
                response.userName()
        );

        context.setUserName(response.userName());
        context.setUserId(response.userId());
        context.setState(ConnectionState.PLAY);

        sender.send(context, new LoginSuccessResponsePacket(Instant.now(), response.roles()));
    }
}
