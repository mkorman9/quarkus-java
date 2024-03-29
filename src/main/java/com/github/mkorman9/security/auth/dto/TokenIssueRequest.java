package com.github.mkorman9.security.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TokenIssueRequest {
    private UUID userId;

    private String remoteAddress;

    private String device;
}
