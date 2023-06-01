package com.github.mkorman9.security.oauth2.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class OAuth2StateService {
    private final Map<String, Boolean> store = new ConcurrentHashMap<>();

    public String generateState() {
        var state = UUID.randomUUID().toString();
        store.put(state, true);
        return state;
    }

    public boolean validateState(String state) {
        return store.remove(state) != null;
    }
}
