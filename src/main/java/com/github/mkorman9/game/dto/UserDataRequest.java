package com.github.mkorman9.game.dto;

import java.util.UUID;

public record UserDataRequest(
        UUID id
) {
    public static final String NAME = "userDataRequests";
}
