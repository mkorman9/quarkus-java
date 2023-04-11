package com.github.mkorman9.security.auth.dto;

import com.github.mkorman9.security.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.websocket.Session;

@Data
@AllArgsConstructor
public class UserEventsSocketConnection {
    private Session session;

    private User user;
}
