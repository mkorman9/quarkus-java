package com.github.mkorman9.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/error")
public class InternalErrorResource {
    @GET
    public String throwError() {
        throw new RuntimeException();
    }
}
