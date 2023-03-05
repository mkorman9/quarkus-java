package com.github.mkorman9.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="user_roles")
@Data
@NoArgsConstructor
public class UserRole {
    @Id
    @GenericGenerator(
            name = "userRoleIdGenerator",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @GeneratedValue(generator = "userRoleIdGenerator")
    @Column(name="id")
    private UUID id;

    @Column(name="role")
    private String role;
}
