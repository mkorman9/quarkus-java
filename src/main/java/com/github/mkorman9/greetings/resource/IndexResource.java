package com.github.mkorman9.greetings.resource;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.Optional;

@Path("/")
public class IndexResource {
    @Inject
    Template index;

    @GET
    public TemplateInstance index(@QueryParam("name") Optional<String> name) {
        return index.data("name", name.orElse("world"));
    }
}
