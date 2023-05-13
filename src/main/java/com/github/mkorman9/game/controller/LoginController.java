package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.*;
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
                    context.disconnect(PlayerDisconnectReason.SERVER_ERROR);
                });
    }

    private void performLogin(PlayerContext context, TokenVerificationResponse response) {
        if (!response.isVerified()) {
            LOG.info("{} login failed", context.getSocket().remoteAddress().hostAddress());

            sender.send(context, new LoginFailedResponsePacket("Login Failed"))
                    .onSuccess(v -> context.disconnect(PlayerDisconnectReason.LOGIN_FAILED));

            return;
        }

        LOG.info(
                "{} logged in as {}",
                context.getSocket().remoteAddress().hostAddress(),
                response.getUserName()
        );

        context.setUserName(response.getUserName());
        context.setUserId(response.getUserId());
        context.setState(ConnectionState.PLAY);

        sender.send(context, new LoginSuccessResponsePacket(Instant.now(), response.getRoles()));
    }
}
