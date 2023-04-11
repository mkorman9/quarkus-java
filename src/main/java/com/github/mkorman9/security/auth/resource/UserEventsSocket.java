package com.github.mkorman9.security.auth.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkorman9.security.auth.dto.UserEvent;
import com.github.mkorman9.security.auth.dto.UserEventsSocketConnection;
import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.TokenAuthenticationService;
import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/user/events")
@ApplicationScoped
public class UserEventsSocket {
    private static final Logger LOG = LoggerFactory.getLogger(UserEventsSocket.class);
    private static final String TOKEN_URL_PARAM = "token";

    @Inject
    TokenAuthenticationService tokenAuthenticationService;

    @Inject
    ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, UserEventsSocketConnection> connections = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        var maybeUser = authenticateUser(session);
        if (maybeUser.isEmpty()) {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not authorized"));
            return;
        }

        var user = maybeUser.get();
        connections.put(session.getId(), new UserEventsSocketConnection(session, user));

        LOG.info("{} connected as {}", user.getName(), session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        connections.remove(session.getId());

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

    @ConsumeEvent(UserEvent.TOPIC_NAME)
    public void onUserEvent(UserEvent event) {
        connections.values().forEach(s -> {
            sendMessage(s, event);
        });
    }

    private Optional<User> authenticateUser(Session session) {
        var bearerTokenValues = session.getRequestParameterMap().get(TOKEN_URL_PARAM);
        if (bearerTokenValues == null || bearerTokenValues.isEmpty()) {
            return Optional.empty();
        }

        return tokenAuthenticationService.authenticate(bearerTokenValues.get(0))
                .map(securityContext -> (User) securityContext.getUserPrincipal());
    }

    private void sendMessage(UserEventsSocketConnection connection, Object message) {
        try {
            connection.getSession()
                    .getAsyncRemote()
                    .sendText(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to convert message to JSON", e);
        }
    }
}
