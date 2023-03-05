package com.github.mkorman9.security.resource;

import com.github.mkorman9.security.model.User;
import com.github.mkorman9.security.service.UserService;
import org.jboss.resteasy.reactive.RestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
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
    @RolesAllowed({"ADMIN"})
    public String addUser(@RestPath String name) {
        var executiveUser = (User) securityContext.getUserPrincipal();
        LOG.info("{} has added new user: {}", executiveUser.getName(), name);

        userService.addUser(name);
        return "OK";
    }
}
