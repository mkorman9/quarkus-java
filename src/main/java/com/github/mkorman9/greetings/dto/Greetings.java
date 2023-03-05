package com.github.mkorman9.greetings.dto;

import lombok.Builder;

@Builder
public record Greetings(
        String text
) {
}
