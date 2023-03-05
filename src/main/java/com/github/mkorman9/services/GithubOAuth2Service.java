package com.github.mkorman9.services;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class GithubOAuth2Service {
    private final OAuth20Service service;
    private final OAuth2StateService stateService;

    @Inject
    public GithubOAuth2Service(
            @ConfigProperty(name="oauth2.github.clientId") String clientId,
            @ConfigProperty(name="oauth2.github.clientSecret") String clientSecret,
            @ConfigProperty(name="oauth2.github.redirectUrl") String redirectUrl,
            OAuth2StateService stateService
    ) {
        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(redirectUrl)
                .build(GitHubApi.instance());
        this.stateService = stateService;
    }

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl(stateService.generateState());
    }

    public Optional<OAuth2AccessToken> resolveAccessToken(String code, String state) {
        if (!stateService.validateState(state)) {
            return Optional.empty();
        }

        try {
            return Optional.of(service.getAccessToken(code));
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (OAuthException e) {
            return Optional.empty();
        }
    }
}
