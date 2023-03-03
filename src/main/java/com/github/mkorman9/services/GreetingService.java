package com.github.mkorman9.services;

import com.github.mkorman9.models.Greetings;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {
    @ConfigProperty(name="greeting.message")
    String message;

    public Greetings generate() {
        return new Greetings(message);
    }
}
