package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.model.UserRole;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {
    @Inject
    EntityManager entityManager;

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
        return user;
    }

    @Transactional
    public boolean assignRole(UUID id, String role) {
        var maybeUser = getById(id);
        if (maybeUser.isEmpty()) {
            return false;
        }

        var user = maybeUser.get();
        var roleEntity = new UserRole();
        roleEntity.setRole(role);

        user.getRoles().add(roleEntity);
        entityManager.merge(user);

        return true;
    }
}
