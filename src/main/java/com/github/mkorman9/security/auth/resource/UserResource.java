package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.dto.AssignRoleRequest;
import com.github.mkorman9.security.auth.exception.RoleAlreadyAssignedException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.SessionService;
import com.github.mkorman9.security.auth.service.UserService;
import io.smallrye.common.annotation.Blocking;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;

@Path("/user")
public class UserResource {
    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @Inject
    SessionService sessionService;

    @Context
    SecurityContext securityContext;

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
    @Blocking
    public void assignRole(@RestPath UUID id, @Valid @NotNull AssignRoleRequest request) {
        var executiveUser = (User) securityContext.getUserPrincipal();

        try {
            userService.assignRole(id, request.getRole());
            LOG.info("{} has added new role {} to user: {}", executiveUser.getName(), request.getRole(), id);
        } catch (UserNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (RoleAlreadyAssignedException e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("{id}/token")
    public String getUserToken(@RestPath UUID id) {
        var maybeUser = userService.getById(id);
        if (maybeUser.isEmpty()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        return sessionService.newToken(maybeUser.get());
    }
}
