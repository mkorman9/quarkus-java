package com.github.mkorman9.security.auth.resource;

import com.github.mkorman9.security.auth.dto.TokenIssueRequest;
import com.github.mkorman9.security.auth.dto.UserDto;
import com.github.mkorman9.security.auth.exception.RoleAlreadyAssignedException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import com.github.mkorman9.security.auth.dto.payload.AssignRolePayload;
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

    @GET
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @POST
    @Path("{name}")
    @RolesAllowed({"ADMIN"})
    public UUID addUser(
            @RestPath String name,
            @Context SecurityContext securityContext
    ) {
        var principal = (UserDto) securityContext.getUserPrincipal();

        var user = userService.addUser(name);
        log.info("{} has added new user: {}", principal.getName(), name);

        return user.getId();
    }

    @POST
    @Path("{id}/roles")
    @RolesAllowed({"ADMIN"})
    @Blocking
    public void assignRole(
            @RestPath UUID id,
            @Valid @NotNull AssignRolePayload payload,
            @Context SecurityContext securityContext
    ) {
        var principal = (UserDto) securityContext.getUserPrincipal();

        try {
            userService.assignRole(id, payload.getRole());
            log.info("{} has added new role {} to user: {}", principal.getName(), payload.getRole(), id);
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
        var tokenRequest = TokenIssueRequest.builder()
                .userId(id)
                .remoteAddress(request.remoteAddress().hostAddress())
                .device(deviceHeader.orElse(""))
                .build();

        var maybeToken = tokenService.issueToken(tokenRequest);
        if (maybeToken.isEmpty()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        return maybeToken.get().getToken();
    }
}
