package com.github.mkorman9.game.dto;

public record TokenVerificationRequest(
        String token
) {
    public static final String NAME = "tokenVerification";
}
