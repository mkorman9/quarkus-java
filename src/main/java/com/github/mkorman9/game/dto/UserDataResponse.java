package com.github.mkorman9.game.dto;

import java.util.Set;

public record UserDataResponse(
        boolean found,
        Set<String> roles
) {
}
