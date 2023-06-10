package com.github.mkorman9.security.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name="tokens")
@Data
@NoArgsConstructor
public class Token {
    @Id
    @Column(name="token")
    private String token;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name="issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name="remote_address", nullable = false)
    private String remoteAddress;

    @Column(name="device", nullable = false)
    private String device;

    @Column(name="valid", nullable = false)
    private boolean isValid;
}
