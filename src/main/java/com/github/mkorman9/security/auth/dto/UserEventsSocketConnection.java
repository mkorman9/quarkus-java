package com.github.mkorman9.security.auth.dto;

import com.github.mkorman9.security.auth.model.User;

import javax.websocket.Session;

public record UserEventsSocketConnection(
        Session session,
        User user
) {
}
