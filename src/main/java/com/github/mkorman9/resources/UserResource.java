package com.github.mkorman9.resources;

import com.github.mkorman9.models.User;
import com.github.mkorman9.services.UserService;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/user")
public class UserResource {
    private final UserService userService;

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @POST
    @Path("{name}")
    public String addUser(@RestPath String name) {
        userService.addUser(name);
        return "OK";
    }
}
