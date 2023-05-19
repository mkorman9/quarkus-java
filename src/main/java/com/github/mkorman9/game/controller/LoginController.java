package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.*;
import com.github.mkorman9.game.dto.packet.login.LoginFailedResponsePacket;
import com.github.mkorman9.game.dto.packet.login.LoginPacket;
import com.github.mkorman9.game.dto.packet.login.LoginSuccessResponsePacket;
import com.github.mkorman9.game.service.PacketSender;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
@Slf4j
public class LoginController {
    @Inject
    PacketSender sender;

    @Inject
    EventBus eventBus;

    public void onLogin(PlayerContext context, LoginPacket packet) {
        var request = new TokenVerificationRequest(packet.getToken());
        eventBus.<TokenVerificationResponse>request(TokenVerificationRequest.NAME, request)
                .onSuccess(m -> performLogin(context, m.body()))
                .onFailure(t -> {
                    log.error("Error while verifying token", t);
                    context.disconnect(PlayerDisconnectReason.SERVER_ERROR);
                });
    }

    private void performLogin(PlayerContext context, TokenVerificationResponse response) {
        if (!response.isVerified()) {
            log.info("{} login failed", context.getSocket().remoteAddress().hostAddress());

            sender.send(context, new LoginFailedResponsePacket("Login Failed"))
                    .onSuccess(v -> context.disconnect(PlayerDisconnectReason.LOGIN_FAILED));

            return;
        }

        log.info(
                "{} logged in as {}",
                context.getSocket().remoteAddress().hostAddress(),
                response.getUserName()
        );

        context.setUserInfo(new UserInfo(response.getUserId(), response.getUserName()));
        context.setState(ConnectionState.PLAY);

        sender.send(context, new LoginSuccessResponsePacket(Instant.now(), response.getRoles()));
    }
}
