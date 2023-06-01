package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.dto.UserEvent;
import com.github.mkorman9.security.auth.exception.RoleAlreadyAssignedException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.model.UserRole;
import io.vertx.core.eventbus.EventBus;
import org.hibernate.exception.ConstraintViolationException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {
    @Inject
    EntityManager entityManager;

    @Inject
    EventBus eventBus;

    @Transactional
    public Optional<User> getById(UUID id) {
        var maybeUser = entityManager.find(User.class, id);
        return Optional.ofNullable(maybeUser);
    }

    @Transactional
    public List<User> getAllUsers() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    @Transactional
    public User addUser(String name) {
        var user = new User();
        user.setName(name);
        user.setCreatedAt(Instant.now());

        entityManager.persist(user);
        eventBus.publish(UserEvent.NAME, new UserEvent(user.getId(), Instant.now(), UserEvent.EventType.CREATED));

        return user;
    }

    @Transactional
    public void assignRole(UUID id, String role) {
        var maybeUser = getById(id);
        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        var user = maybeUser.get();
        var roleEntity = new UserRole();
        roleEntity.setRole(role);
        user.getRoles().add(roleEntity);

        try {
            entityManager.merge(user);
            entityManager.flush();
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException violation) {
                if (violation.getConstraintName().equals(UserRole.UNIQUE_CONSTRAINT)) {
                    throw new RoleAlreadyAssignedException();
                }
            }

            throw e;
        }
    }
}
