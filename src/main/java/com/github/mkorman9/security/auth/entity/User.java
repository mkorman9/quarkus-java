package com.github.mkorman9.security.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size=10)
    private List<UserRole> roles = new ArrayList<>();
}
