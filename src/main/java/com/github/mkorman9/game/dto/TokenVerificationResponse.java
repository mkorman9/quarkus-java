package com.github.mkorman9.game.dto;

import java.util.Set;
import java.util.UUID;

public record TokenVerificationResponse(
        boolean verified,
        UUID userId,
        String userName,
        Set<String> roles
) {
}
