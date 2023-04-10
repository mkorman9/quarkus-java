package com.github.mkorman9.security.auth.resource.auth;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;
import java.util.Optional;

public class BearerTokenExtractor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    public static Optional<String> extract(ContainerRequestContext context) {
        List<String> authHeaderValues = context
                .getHeaders()
                .get(AUTHORIZATION_HEADER);

        if (authHeaderValues == null || authHeaderValues.isEmpty()) {
            return Optional.empty();
        }

        var headerValue = authHeaderValues.get(0);
        var headerParts = headerValue.split("\\s+");

        if (headerParts.length != 2 || !headerParts[0].equalsIgnoreCase(TOKEN_TYPE)) {
            return Optional.empty();
        }

        var token = headerParts[1];
        return Optional.of(token);
    }
}
