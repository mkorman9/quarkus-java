package com.github.mkorman9.game.controller;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.PlayerDisconnectReason;
import com.github.mkorman9.game.dto.TokenVerificationRequest;
import com.github.mkorman9.game.dto.TokenVerificationResponse;
import com.github.mkorman9.game.dto.UserInfo;
import com.github.mkorman9.game.dto.packet.login.LoginFailedResponsePacket;
import com.github.mkorman9.game.dto.packet.login.LoginPacket;
import com.github.mkorman9.game.dto.packet.login.LoginSuccessResponsePacket;
import com.github.mkorman9.game.service.PacketSender;
import com.github.mkorman9.game.service.TokenVerificationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@ApplicationScoped
@Slf4j
public class LoginController {
    @Inject
    PacketSender sender;

    @Inject
    TokenVerificationService tokenVerificationService;

    public void onLogin(PlayerContext context, LoginPacket packet) {
        var request = new TokenVerificationRequest(packet.getToken());
        tokenVerificationService.verifyToken(request)
                .onSuccess(r -> performLogin(context, r))
                .onFailure(t -> {
                    log.error("Error while verifying token", t);
                    context.disconnect(PlayerDisconnectReason.SERVER_ERROR);
                });
    }

    private void performLogin(PlayerContext context, TokenVerificationResponse response) {
        if (!response.isVerified()) {
            log.info("Login failed for {}", context.getSocket().remoteAddress().hostAddress());

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
