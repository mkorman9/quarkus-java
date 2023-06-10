package com.github.mkorman9.greetings.service;

import com.github.mkorman9.greetings.dto.Greetings;
import io.vertx.core.Future;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GreetingService {
    @ConfigProperty(name="greeting.message")
    String message;

    public Future<Greetings> generate() {
        return Future.succeededFuture(new Greetings(message));
    }
}
