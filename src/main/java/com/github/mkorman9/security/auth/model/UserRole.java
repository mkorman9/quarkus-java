package com.github.mkorman9.security.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="user_roles")
@Data
@NoArgsConstructor
public class UserRole {
    public static final String UNIQUE_CONSTRAINT = "user_roles_unique";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="role", columnDefinition="text")
    private String role;
}
