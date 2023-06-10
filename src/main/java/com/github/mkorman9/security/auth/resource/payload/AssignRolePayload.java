package com.github.mkorman9.security.auth.resource.payload;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AssignRolePayload {
    @NotBlank
    private String role;
}
