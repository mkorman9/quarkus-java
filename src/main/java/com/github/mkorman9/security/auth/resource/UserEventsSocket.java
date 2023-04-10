package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.TokenAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Optional;

@ServerEndpoint(value = "/user/events")
@ApplicationScoped
public class UserEventsSocket {
    private static final Logger LOG = LoggerFactory.getLogger(UserEventsSocket.class);
    private static final String TOKEN_URL_PARAM = "token";

    @Inject
    TokenAuthenticationService tokenAuthenticationService;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        var maybeUser = authenticateUser(session);
        if (maybeUser.isEmpty()) {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not authorized"));
            return;
        }
        var user = maybeUser.get();

        LOG.info("{} connected as {}", user.getName(), session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("{} disconnected", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable t) {
        LOG.info("{} encountered error: {}", session.getId(), t.getMessage());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        LOG.info("{} sent message: {}", session.getId(), message);
    }

    private Optional<User> authenticateUser(Session session) {
        var bearerTokenValues = session.getRequestParameterMap().get(TOKEN_URL_PARAM);
        if (bearerTokenValues == null || bearerTokenValues.isEmpty()) {
            return Optional.empty();
        }

        var maybeSecurityContext = tokenAuthenticationService.authenticate(bearerTokenValues.get(0));
        if (maybeSecurityContext.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((User) maybeSecurityContext.get().getUserPrincipal());
    }
}
