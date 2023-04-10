package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.resource.auth.WebSocketAuthenticationConfigurator;
import com.github.mkorman9.security.auth.service.TokenAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Optional;

@ServerEndpoint(value = "/user/events", configurator = WebSocketAuthenticationConfigurator.class)
@ApplicationScoped
public class UserEventsSocket {
    private static final Logger LOG = LoggerFactory.getLogger(UserEventsSocket.class);

    @Inject
    TokenAuthenticationService tokenAuthenticationService;

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) throws IOException {
        var maybeUser = authenticateUser(endpointConfig);
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

    private Optional<User> authenticateUser(EndpointConfig endpointConfig) {
        var maybeBearerToken = WebSocketAuthenticationConfigurator.getBearerToken(endpointConfig);
        if (maybeBearerToken.isPresent()) {
            var maybeSecurityContext = tokenAuthenticationService.authenticate(maybeBearerToken.get());
            if (maybeSecurityContext.isPresent()) {
                return Optional.of((User) maybeSecurityContext.get().getUserPrincipal());
            }
        }

        return Optional.empty();
    }
}
