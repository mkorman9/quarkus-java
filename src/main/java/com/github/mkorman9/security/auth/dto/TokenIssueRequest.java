package com.github.mkorman9.security.auth.dto;

import com.github.mkorman9.security.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenIssueRequest {
    private User user;

    private String remoteAddress;

    private String device;
}
