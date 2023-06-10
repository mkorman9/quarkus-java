package com.github.mkorman9.security.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenIssueRequest {
    private UUID userId;

    private String remoteAddress;

    private String device;
}
