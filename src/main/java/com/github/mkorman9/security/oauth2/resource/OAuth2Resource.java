package com.github.mkorman9.security.oauth2.resource;

import com.github.mkorman9.security.oauth2.dto.GithubUserInfo;
import com.github.mkorman9.security.oauth2.service.GithubOAuth2Service;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestQuery;

import java.net.URI;

@Path("/oauth2")
public class OAuth2Resource {
    private final GithubOAuth2Service githubOAuth2Service;

    @Inject
    public OAuth2Resource(GithubOAuth2Service githubOAuth2Service) {
        this.githubOAuth2Service = githubOAuth2Service;
    }

    @GET
    @Path("github/login")
    public Response githubLogin() {
        return Response
                .seeOther(URI.create(githubOAuth2Service.getAuthorizationUrl()))
                .build();
    }

    @GET
    @Path("github/callback")
    public GithubUserInfo githubCallback(
            @RestQuery String code,
            @RestQuery String state
    ) {
        var maybeAccessToken = githubOAuth2Service.retrieveAccessToken(code, state);
        if (maybeAccessToken.isEmpty()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        var accessToken = maybeAccessToken.get();
        return githubOAuth2Service.retrieveUserInfo(accessToken);
    }
}
