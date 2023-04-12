package com.github.mkorman9.greetings.resource;

import com.github.mkorman9.greetings.dto.Greetings;
import com.github.mkorman9.greetings.service.GreetingService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class GreetingResource {

    private final GreetingService greetingService;

    @Inject
    public GreetingResource(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GET
    public Uni<Greetings> greet() {
        var greetings = greetingService.generate();
        return Uni.createFrom().item(greetings);
    }
}
