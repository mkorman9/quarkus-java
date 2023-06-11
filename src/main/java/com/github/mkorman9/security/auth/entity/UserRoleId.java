package com.github.mkorman9.security.auth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserRoleId implements Serializable {
    private User user;

    private String role;
}
