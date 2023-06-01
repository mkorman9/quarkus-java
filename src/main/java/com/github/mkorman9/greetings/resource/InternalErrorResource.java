package com.github.mkorman9.greetings.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/error")
public class InternalErrorResource {
    @GET
    public String throwError() {
        throw new RuntimeException();
    }
}
