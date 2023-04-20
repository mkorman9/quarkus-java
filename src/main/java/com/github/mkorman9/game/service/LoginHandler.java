package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.ConnectionState;
import com.github.mkorman9.game.dto.PlayerContext;
import com.github.mkorman9.game.dto.packet.login.LoginFailedResponsePacket;
import com.github.mkorman9.game.dto.packet.login.LoginPacket;
import com.github.mkorman9.game.dto.packet.login.LoginSuccessResponsePacket;
import com.github.mkorman9.security.auth.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class LoginHandler {
    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);

    @Inject
    PacketSender sender;

    @Inject
    TokenService tokenService;

    public void onLogin(PlayerContext context, LoginPacket packet) {
        var maybeToken = tokenService.verifyToken(packet.getToken());
        if (maybeToken.isEmpty()) {
            LOG.info("{} login failed", context.getSocket().remoteAddress().hostAddress());
            sender.send(context, new LoginFailedResponsePacket("Login Failed"));
            return;
        }

        var token = maybeToken.get();
        context.setUserName(token.getName());
        context.setUserId(token.getUserId());

        LOG.info(
                "{} logged in as {}",
                context.getSocket().remoteAddress().hostAddress(),
                context.getUserName()
        );

        context.setState(ConnectionState.PLAY);
        sender.send(context, new LoginSuccessResponsePacket(Instant.now()));
    }
}
