package com.github.mkorman9.resources;

import com.github.mkorman9.services.GithubOAuth2Service;
import org.jboss.resteasy.reactive.RestQuery;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
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
    public Response githubCallback(
            @RestQuery String code,
            @RestQuery String state
    ) {
        var maybeAccessToken = githubOAuth2Service.resolveAccessToken(code, state);
        if (maybeAccessToken.isEmpty()) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .build();
        }

        var accessToken = maybeAccessToken.get();
        var userInfo = githubOAuth2Service.resolveUserInfo(accessToken);

        return Response
                .ok(userInfo)
                .build();
    }
}