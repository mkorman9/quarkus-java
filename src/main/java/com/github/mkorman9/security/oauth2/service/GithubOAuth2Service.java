package com.github.mkorman9.security.oauth2.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkorman9.security.oauth2.dto.GithubUserEmailResponse;
import com.github.mkorman9.security.oauth2.dto.GithubUserInfo;
import com.github.mkorman9.security.oauth2.dto.GithubUserInfoResponse;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class GithubOAuth2Service {
    private static final String USER_INFO_URL = "https://api.github.com/user";
    private static final String USER_EMAIL_URL = "https://api.github.com/user/public_emails";
    private static final String EMAIL_SCOPE = "user:email";

    private final OAuth20Service service;
    private final OAuth2StateService stateService;
    private final ObjectMapper objectMapper;

    @Inject
    public GithubOAuth2Service(
            @ConfigProperty(name="oauth2.github.clientId") String clientId,
            @ConfigProperty(name="oauth2.github.clientSecret") String clientSecret,
            @ConfigProperty(name="oauth2.github.redirectUrl") String redirectUrl,
            OAuth2StateService stateService,
            ObjectMapper objectMapper
    ) {
        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(redirectUrl)
                .build(GitHubApi.instance());
        this.stateService = stateService;
        this.objectMapper = objectMapper;
    }

    public String getAuthorizationUrl() {
        return service.createAuthorizationUrlBuilder()
                .state(stateService.generateState())
                .scope(EMAIL_SCOPE)
                .build();
    }

    @SneakyThrows
    public Optional<OAuth2AccessToken> retrieveAccessToken(String code, String state) {
        if (!stateService.validateState(state)) {
            return Optional.empty();
        }

        try {
            return Optional.of(service.getAccessToken(code));
        } catch (OAuthException e) {
            return Optional.empty();
        }
    }

    public GithubUserInfo retrieveUserInfo(OAuth2AccessToken accessToken) {
        var userInfo = retrieveUserInfoResponse(accessToken);
        var userEmails = retrieveUserEmailResponses(accessToken);
        return GithubUserInfo.builder()
                .id(userInfo.getId())
                .login(userInfo.getLogin())
                .name(userInfo.getName())
                .email(selectUserEmail(userEmails))
                .avatarUrl(userInfo.getAvatarUrl())
                .build();
    }

    private static String selectUserEmail(List<GithubUserEmailResponse> userEmails) {
        String candidate = "";
        for (var email : userEmails) {
            if (email.isPrimary() && email.isVerified()) {
                return email.getEmail();  // return primary, verified email immediately
            }

            if (email.isVerified()) {
                candidate = email.getEmail();
            }
        }

        return candidate;
    }

    private GithubUserInfoResponse retrieveUserInfoResponse(OAuth2AccessToken accessToken) {
        var request = new OAuthRequest(Verb.GET, USER_INFO_URL);
        service.signRequest(accessToken, request);

        try (var response = service.execute(request)) {
            var body = response.getBody();
            return objectMapper.readValue(body, GithubUserInfoResponse.class);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GithubUserEmailResponse> retrieveUserEmailResponses(OAuth2AccessToken accessToken) {
        var request = new OAuthRequest(Verb.GET, USER_EMAIL_URL);
        service.signRequest(accessToken, request);

        try (var response = service.execute(request)) {
            var body = response.getBody();
            return objectMapper.readValue(body, new TypeReference<>(){});
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
