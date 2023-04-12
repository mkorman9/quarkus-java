package com.github.mkorman9.security.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name="tokens")
@Data
@NoArgsConstructor
public class Token {
    @Id
    @Column(name="token", columnDefinition="text")
    private String token;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name="issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name="valid", nullable = false)
    private boolean isValid;
}
