package com.github.mkorman9.security.auth.resource.auth;

import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Optional;

public class WebSocketAuthenticationConfigurator extends ServerEndpointConfig.Configurator {
    private static final String BEARER_TOKEN_PROPERTY = "bearerToken";

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        var maybeToken = BearerTokenExtractor.extract(request);
        sec.getUserProperties().put(BEARER_TOKEN_PROPERTY, maybeToken);
    }

    public static Optional<String> getBearerToken(EndpointConfig endpointConfig) {
        return (Optional<String>) endpointConfig.getUserProperties().get(BEARER_TOKEN_PROPERTY);
    }
}
