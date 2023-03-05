package com.github.mkorman9.resources.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GithubUserInfoResponse {
    private long id;

    private String login;

    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;
}
