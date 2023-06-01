package com.github.mkorman9.security.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AssignRoleRequest {
    @NotBlank
    private String role;
}
