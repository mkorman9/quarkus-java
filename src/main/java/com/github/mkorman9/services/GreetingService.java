package com.github.mkorman9.services;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {
    @ConfigProperty(name="greeting.message")
    String message;

    public String getMessage() {
        return message;
    }
}
