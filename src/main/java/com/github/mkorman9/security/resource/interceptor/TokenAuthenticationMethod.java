package com.github.mkorman9.security.resource.interceptor;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.SecurityContext;

public interface TokenAuthenticationMethod {
    Uni<SecurityContext> authenticate(String token);
}
