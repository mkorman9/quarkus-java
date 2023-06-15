package com.github.mkorman9.security.auth.dto.converter;

import com.github.mkorman9.security.auth.dto.TokenDto;
import com.github.mkorman9.security.auth.entity.Token;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TokenDtoConverter {
    @Inject
    UserDtoConverter userDtoConverter;

    public TokenDto convertToDto(Token token) {
        return TokenDto.builder()
                .token(token.getToken())
                .owner(userDtoConverter.convertToDto(token.getOwner()))
                .issuedAt(token.getIssuedAt())
                .remoteAddress(token.getRemoteAddress())
                .device(token.getDevice())
                .build();
    }
}
