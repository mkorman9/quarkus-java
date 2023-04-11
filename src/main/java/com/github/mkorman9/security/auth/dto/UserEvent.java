package com.github.mkorman9.security.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private UUID userId;

    private Instant timestamp;

    private EventType eventType;

    public enum EventType {
        CREATED
    }
}
