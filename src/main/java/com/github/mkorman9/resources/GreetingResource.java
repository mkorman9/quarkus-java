package com.github.mkorman9.resources;

import com.github.mkorman9.models.Greetings;
import com.github.mkorman9.services.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class GreetingResource {
    private static final Logger LOG = LoggerFactory.getLogger(GreetingResource.class);

    private final GreetingService greetingService;

    @Inject
    public GreetingResource(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GET
    public Greetings greet() {
        LOG.info("Greetings!");
        return greetingService.generate();
    }
}
