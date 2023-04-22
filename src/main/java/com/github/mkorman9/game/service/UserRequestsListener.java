package com.github.mkorman9.game.service;

import com.github.mkorman9.game.dto.UserDataRequest;
import com.github.mkorman9.game.dto.UserDataResponse;
import com.github.mkorman9.security.auth.service.UserService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserRequestsListener {
    @Inject
    UserService userService;

    @ConsumeEvent(UserDataRequest.NAME)
    @Blocking
    public UserDataResponse onUserDataRequest(UserDataRequest request) {
        var maybeUser = userService.getById(request.id());
        if (maybeUser.isEmpty()) {
            return new UserDataResponse(false, null);
        }

        var user = maybeUser.get();
        return new UserDataResponse(true, user.getRolesSet());
    }
}
