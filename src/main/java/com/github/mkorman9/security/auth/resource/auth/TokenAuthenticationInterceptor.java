package com.github.mkorman9.security.auth.resource.auth;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.mkorman9.security.auth.service.JWTHelper;
import com.github.mkorman9.security.auth.service.UserAuthenticationService;
import io.quarkus.runtime.ExecutorRecorder;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

public class TokenAuthenticationInterceptor {
    private final JWTVerifier verifier;
    private final UserAuthenticationService userAuthenticationService;

    @Inject
    public TokenAuthenticationInterceptor(JWTHelper jwt, UserAuthenticationService userAuthenticationService) {
        this.verifier = jwt.getVerification().build();
        this.userAuthenticationService = userAuthenticationService;
    }

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Void> intercept(ContainerRequestContext context) {
        var maybeToken = BearerTokenExtractor.extract(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        try {
            var decodedToken = verifier.verify(maybeToken.get());
            var uid = decodedToken.getClaim("uid").asString();

            if (uid == null) {
                return Uni.createFrom().voidItem();
            }

            return authenticate(uid)
                    .map(securityContext -> {
                        context.setSecurityContext(securityContext);
                        return null;
                    })
                    .replaceWithVoid()
                    .onFailure().recoverWithNull().replaceWithVoid();
        } catch (JWTVerificationException e) {
            return Uni.createFrom().voidItem();
        }
    }

    private Uni<SecurityContext> authenticate(String uid) {
        return Uni.createFrom().deferred(() ->
                Uni.createFrom().emitter(uniEmitter ->
                        ExecutorRecorder.getCurrent().execute(() ->
                                userAuthenticationService.authenticate(uid).ifPresentOrElse(
                                        uniEmitter::complete,
                                        () -> uniEmitter.fail(new IllegalArgumentException())
                                )
                        )
                )
        );
    }
}
