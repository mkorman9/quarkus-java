package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.dto.UserEvent;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

@ServerEndpoint(value = "/user/events")
@ApplicationScoped
@Slf4j
public class UserEventsSocket {
    /*
    private static final String TOKEN_URL_PARAM = "token";

    @Inject
    TokenService tokenService;

    @Inject
    UserService userService;

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

        log.info("{} connected as {}", user.getName(), session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        connections.remove(session.getId());

        log.info("{} disconnected", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable t) {
        log.info("{} encountered error: {}", session.getId(), t.getMessage());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("{} sent message: {}", session.getId(), message);
    }

    @ConsumeEvent(UserEvent.NAME)
    public void onUserEvent(UserEvent event) {
        connections.values().forEach(c -> {
            sendMessage(c, event);
        });
    }

    private Optional<User> authenticateUser(Session session) {
        var bearerTokenValues = session.getRequestParameterMap().get(TOKEN_URL_PARAM);
        if (bearerTokenValues == null || bearerTokenValues.isEmpty()) {
            return Optional.empty();
        }

        var bearerToken = bearerTokenValues.get(0);
        var maybeTokenPrincipal = tokenService.verifyToken(bearerToken);
        if (maybeTokenPrincipal.isEmpty()) {
            return Optional.empty();
        }

        var tokenPrincipal = maybeTokenPrincipal.get();

        try {
            return userService.getById(tokenPrincipal.getUserId());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private void sendMessage(UserEventsSocketConnection connection, Object message) {
        try {
            connection.session()
                    .getAsyncRemote()
                    .sendText(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Failed to convert message to JSON", e);
        }
    }
    */

    @ConsumeEvent(UserEvent.NAME)
    public void onUserEvent(UserEvent event) {
        // left in order for codec for UserEvent to be registered
    }
}
