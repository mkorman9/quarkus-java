package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.dto.UserDto;
import com.github.mkorman9.security.auth.dto.UserEvent;
import com.github.mkorman9.security.auth.dto.converter.UserDtoConverter;
import com.github.mkorman9.security.auth.entity.User;
import com.github.mkorman9.security.auth.entity.UserRole;
import com.github.mkorman9.security.auth.exception.NameAlreadyExistsException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {
    @Inject
    EntityManager entityManager;

    @Inject
    EventBus eventBus;

    @Inject
    UserDtoConverter userDtoConverter;

    @Transactional
    public List<UserDto> getAllUsers() {
        return entityManager.createQuery("from User", User.class)
                .getResultList()
                .stream()
                .map(userDtoConverter::convertToDto)
                .toList();
    }

    @Transactional
    public UserDto addUser(String name) {
        var user = new User();
        user.setName(name);
        user.setCreatedAt(Instant.now());

        try {
            entityManager.persist(user);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            if (e.getConstraintName().equals(User.CONSTRAINT_UNIQUE_NAME)) {
                throw new NameAlreadyExistsException();
            }

            throw e;
        }

        eventBus.publish(UserEvent.NAME, new UserEvent(user.getId(), Instant.now(), UserEvent.EventType.CREATED));

        return userDtoConverter.convertToDto(user);
    }

    @Transactional
    public void assignRole(UUID id, String role) {
        var user = entityManager.find(User.class, id);
        if (user == null) {
            throw new UserNotFoundException();
        }

        var roleEntity = new UserRole();
        roleEntity.setUser(user);
        roleEntity.setRole(role);
        user.getRoles().add(roleEntity);

        entityManager.merge(user);
    }
}
