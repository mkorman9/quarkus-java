package com.github.mkorman9.security.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TokenDto {
    private String token;

    private UserDto user;

    private Instant issuedAt;

    private String remoteAddress;

    private String device;
}
