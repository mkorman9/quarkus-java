package com.github.mkorman9.security.auth.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/user/events")
@ApplicationScoped
public class UserEventsSocket {
    private static final Logger LOG = LoggerFactory.getLogger(UserEventsSocket.class);

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("{} connected", session.getId());
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
}
