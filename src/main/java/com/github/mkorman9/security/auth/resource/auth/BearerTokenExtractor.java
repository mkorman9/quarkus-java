package com.github.mkorman9.security.auth.resource.auth;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

public class BearerTokenExtractor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    public static Optional<String> extract(ContainerRequestContext context) {
        var headerValue = context
                .getHeaders()
                .getFirst(AUTHORIZATION_HEADER);

        if (headerValue == null) {
            return Optional.empty();
        }

        return extract(headerValue);
    }

    private static Optional<String> extract(String headerValue) {
        var headerParts = headerValue.split("\\s+");

        if (headerParts.length != 2 || !headerParts[0].equalsIgnoreCase(TOKEN_TYPE)) {
            return Optional.empty();
        }

        var token = headerParts[1];
        return Optional.of(token);
    }
}
