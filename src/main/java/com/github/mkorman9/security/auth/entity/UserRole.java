package com.github.mkorman9.security.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_roles")
@IdClass(UserRoleId.class)
@Data
@NoArgsConstructor
public class UserRole {
    @Id
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Id
    @Column(name="role", nullable = false)
    private String role;
}
