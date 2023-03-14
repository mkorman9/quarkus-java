package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.dto.AssignRoleRequest;
import com.github.mkorman9.security.auth.exception.RoleAlreadyAssignedException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.SessionService;
import com.github.mkorman9.security.auth.service.UserService;
import org.jboss.resteasy.reactive.RestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;

@Path("/user")
public class UserResource {
    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;
    private final SessionService sessionService;
    private final SecurityContext securityContext;

    @Inject
    public UserResource(
            UserService userService,
            SessionService sessionService,
            @Context SecurityContext securityContext
    ) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.securityContext = securityContext;
    }

    @GET
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @POST
    @Path("{name}")
    @RolesAllowed({"ADMIN"})
    public UUID addUser(@RestPath String name) {
        var executiveUser = (User) securityContext.getUserPrincipal();

        var user = userService.addUser(name);
        LOG.info("{} has added new user: {}", executiveUser.getName(), name);
        return user.getId();
    }

    @POST
    @Path("{id}/roles")
    @RolesAllowed({"ADMIN"})
    public Response assignRole(@RestPath UUID id, @Valid @NotNull AssignRoleRequest request) {
        var executiveUser = (User) securityContext.getUserPrincipal();

        try {
            userService.assignRole(id, request.getRole());
            LOG.info("{} has added new role {} to user: {}", executiveUser.getName(), request.getRole(), id);

            return Response.ok().build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (RoleAlreadyAssignedException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("{id}/token")
    public Response getUserToken(@RestPath UUID id) {
        var maybeUser = userService.getById(id);
        if (maybeUser.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        var token = sessionService.newToken(maybeUser.get());

        return Response.ok()
                .entity(token)
                .build();
    }
}
