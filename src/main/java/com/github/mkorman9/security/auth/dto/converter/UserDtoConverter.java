package com.github.mkorman9.security.auth.dto.converter;

import com.github.mkorman9.security.auth.dto.UserDto;
import com.github.mkorman9.security.auth.entity.User;
import com.github.mkorman9.security.auth.entity.UserRole;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.stream.Collectors;

@ApplicationScoped
public class UserDtoConverter {
    public UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles()
                        .stream()
                        .map(UserRole::getRole)
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
