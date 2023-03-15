package com.github.mkorman9.security.auth.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.security.Principal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
public class User implements Principal {
    @Id
    @GenericGenerator(
            name = "userIdGenerator",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @GeneratedValue(generator = "userIdGenerator")
    @Column(name="id")
    private UUID id;

    @Column(name="name")
    private String name;

    @Column(name="created_at")
    private Instant createdAt;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "user_id", nullable = false)
    @BatchSize(size=10)
    @JsonIgnore
    private Set<UserRole> roles;

    @JsonGetter("roles")
    public Set<String> getRolesSet() {
        return roles.stream()
            .map(UserRole::getRole)
            .collect(Collectors.toSet());
    }
}
