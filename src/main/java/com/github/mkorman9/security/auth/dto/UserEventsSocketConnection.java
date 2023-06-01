package com.github.mkorman9.security.auth.dto;

import com.github.mkorman9.security.auth.model.User;

import jakarta.websocket.Session;

public record UserEventsSocketConnection(
        Session session,
        User user
) {
}
