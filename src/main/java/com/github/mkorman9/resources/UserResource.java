package com.github.mkorman9.resources;

import com.github.mkorman9.models.User;
import com.github.mkorman9.services.UserService;
import io.quarkus.security.Authenticated;
import org.jboss.resteasy.reactive.RestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/user")
public class UserResource {
    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;
    private final SecurityContext securityContext;

    @Inject
    public UserResource(
            UserService userService,
            @Context SecurityContext securityContext
    ) {
        this.userService = userService;
        this.securityContext = securityContext;
    }

    @GET
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @POST
    @Path("{name}")
    @Authenticated
    public String addUser(@RestPath String name) {
        LOG.info("{} has added new user: {}", securityContext.getUserPrincipal().getName(), name);

        userService.addUser(name);
        return "OK";
    }
}
