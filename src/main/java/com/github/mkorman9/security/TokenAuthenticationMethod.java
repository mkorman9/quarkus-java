package com.github.mkorman9.security;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.SecurityContext;
import java.util.Optional;

public interface TokenAuthenticationMethod {
    Uni<Optional<SecurityContext>> authenticate(String token);
}
