package com.github.mkorman9.game.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class TokenVerificationResponse {
    private boolean verified;

    private UUID userId;

    private String userName;

    private Set<String> roles;
}
