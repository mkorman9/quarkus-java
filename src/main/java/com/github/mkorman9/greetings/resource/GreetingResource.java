package com.github.mkorman9.greetings.resource;

import com.github.mkorman9.greetings.dto.Greetings;
import com.github.mkorman9.greetings.service.GreetingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.concurrent.CompletionStage;

@Path("/hello")
public class GreetingResource {
    private final GreetingService greetingService;

    @Inject
    public GreetingResource(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GET
    public CompletionStage<Greetings> greet() {
        return greetingService.generate()
                .toCompletionStage();
    }
}
