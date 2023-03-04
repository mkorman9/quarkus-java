package com.github.mkorman9.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

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
}
