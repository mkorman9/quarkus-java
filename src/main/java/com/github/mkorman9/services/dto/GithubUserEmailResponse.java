package com.github.mkorman9.services.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GithubUserEmailResponse {
    private String email;

    private boolean verified;

    private boolean primary;
}
