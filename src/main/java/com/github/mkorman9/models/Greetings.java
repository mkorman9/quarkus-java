package com.github.mkorman9.models;

import lombok.Builder;

@Builder
public record Greetings(
        String text
) {
}
