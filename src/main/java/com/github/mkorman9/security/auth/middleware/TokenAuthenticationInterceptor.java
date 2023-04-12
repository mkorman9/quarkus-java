package com.github.mkorman9.security.auth.middleware;

import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.service.TokenService;
import io.quarkus.runtime.ExecutorRecorder;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class TokenAuthenticationInterceptor {
    @Inject
    TokenService tokenService;

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        var maybeToken = BearerTokenExtractor.extract(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        return authenticate(maybeToken.get())
                .map(securityContext -> {
                    context.setSecurityContext(securityContext);
                    return null;
                })
                .replaceWithVoid()
                .onFailure().recoverWithNull().replaceWithVoid();
    }

    private Uni<SecurityContext> authenticate(String token) {
        return Uni.createFrom().deferred(() ->
                Uni.createFrom().emitter(consumer ->
                        ExecutorRecorder.getCurrent().execute(() ->
                                tokenService.findToken(token).ifPresentOrElse(
                                        t -> consumer.complete(createSecurityContext(t.getUser())),
                                        () -> consumer.fail(new IllegalArgumentException())
                                )
                        )
                )
        );
    }

    private SecurityContext createSecurityContext(User user) {
        var userRoles = user.getRolesSet();

        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return user;
            }

            @Override
            public boolean isUserInRole(String role) {
                return userRoles.contains(role);
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return SecurityContext.DIGEST_AUTH;
            }
        };
    }
}
