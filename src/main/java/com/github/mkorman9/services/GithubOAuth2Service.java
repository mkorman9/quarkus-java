package com.github.mkorman9.services;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class GithubOAuth2Service {
    private final OAuth20Service service;

    @Inject
    public GithubOAuth2Service(
            @ConfigProperty(name="oauth2.github.clientId") String clientId,
            @ConfigProperty(name="oauth2.github.clientSecret") String clientSecret,
            @ConfigProperty(name="oauth2.github.redirectUrl") String redirectUrl
    ) {
        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(redirectUrl)
                .build(GitHubApi.instance());
    }

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl("state");
    }

    public OAuth2AccessToken resolveAccessToken(String code) {
        try {
            return service.getAccessToken(code);
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
