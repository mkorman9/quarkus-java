package com.github.mkorman9.greetings.service;

import com.github.mkorman9.greetings.dto.Greetings;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {
    @ConfigProperty(name="greeting.message")
    String message;

    public Greetings generate() {
        return new Greetings(message);
    }
}
