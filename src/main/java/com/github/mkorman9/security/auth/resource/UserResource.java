package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.dto.AssignRoleRequest;
import com.github.mkorman9.security.auth.dto.TokenIssueRequest;
import com.github.mkorman9.security.auth.exception.RoleAlreadyAssignedException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.TokenService;
import com.github.mkorman9.security.auth.service.UserService;
import io.smallrye.common.annotation.Blocking;
import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.RestPath;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/user")
@Slf4j
public class UserResource {
    @Inject
    UserService userService;

    @Inject
    TokenService tokenService;

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
        var principal = (User) securityContext.getUserPrincipal();

        var user = userService.addUser(name);
        log.info("{} has added new user: {}", principal.getName(), name);

        return user.getId();
    }

    @POST
    @Path("{id}/roles")
    @RolesAllowed({"ADMIN"})
    @Blocking
    public void assignRole(@RestPath UUID id, @Valid @NotNull AssignRoleRequest request) {
        var principal = (User) securityContext.getUserPrincipal();

        try {
            userService.assignRole(id, request.getRole());
            log.info("{} has added new role {} to user: {}", principal.getName(), request.getRole(), id);
        } catch (UserNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (RoleAlreadyAssignedException e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("{id}/token")
    public String issueToken(
            @RestPath UUID id,
            @Context HttpServerRequest request,
            @RestHeader("X-Device") Optional<String> deviceHeader
    ) {
        var maybeUser = userService.getById(id);
        if (maybeUser.isEmpty()) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        var tokenIssueRequest = new TokenIssueRequest();
        tokenIssueRequest.setUserId(id);
        tokenIssueRequest.setRemoteAddress(request.remoteAddress().hostAddress());
        tokenIssueRequest.setDevice(deviceHeader.orElse(""));

        var maybeToken = tokenService.issueToken(tokenIssueRequest);
        if (maybeToken.isEmpty()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        return maybeToken.get().getToken();
    }
}
