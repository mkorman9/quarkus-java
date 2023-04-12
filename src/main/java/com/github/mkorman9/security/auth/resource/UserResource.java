package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.dto.AssignRoleRequest;
import com.github.mkorman9.security.auth.exception.RoleAlreadyAssignedException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import com.github.mkorman9.security.auth.model.Token;
import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.TokenService;
import com.github.mkorman9.security.auth.service.UserService;
import io.smallrye.mutiny.Uni;
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
    TokenService tokenService;

    @Context
    SecurityContext securityContext;

    @GET
    public Uni<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @POST
    @Path("{name}")
    @RolesAllowed({"ADMIN"})
    public Uni<UUID> addUser(@RestPath String name) {
        var executiveUser = (User) securityContext.getUserPrincipal();

        return userService.addUser(name)
                .onItem().invoke(() -> {
                    LOG.info("{} has added new user: {}", executiveUser.getName(), name);
                })
                .map(User::getId);
    }

    @POST
    @Path("{id}/roles")
    @RolesAllowed({"ADMIN"})
    public Uni<Void> assignRole(@RestPath UUID id, @Valid @NotNull AssignRoleRequest request) {
        var executiveUser = (User) securityContext.getUserPrincipal();

        return userService.assignRole(id, request.getRole())
                .onItem().invoke(() -> {
                    LOG.info("{} has added new role {} to user: {}", executiveUser.getName(), request.getRole(), id);
                })
                .onFailure().transform(e -> {
                    if (e instanceof UserNotFoundException) {
                        return new WebApplicationException(Response.Status.NOT_FOUND);
                    } else if (e instanceof RoleAlreadyAssignedException) {
                        return new WebApplicationException(Response.Status.BAD_REQUEST);
                    }

                    return e;
                });
    }

    @GET
    @Path("{id}/token")
    public Uni<String> getUserToken(@RestPath UUID id) {
        return userService.getById(id)
                .onItem().ifNull().failWith(new WebApplicationException(Response.Status.FORBIDDEN))
                .onItem().ifNotNull().transformToUni(user -> {
                    return tokenService.issueToken(user)
                            .map(Token::getToken);
                });
    }
}
