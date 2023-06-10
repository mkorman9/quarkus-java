package com.github.mkorman9.security.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserDto implements Principal {
    private UUID id;

    private String name;

    private Instant createdAt;

    private Set<String> roles;
}
