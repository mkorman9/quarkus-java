package com.github.mkorman9.security.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AssignRoleRequest {
    @NotBlank
    private String role;
}
