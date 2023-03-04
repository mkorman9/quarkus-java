package com.github.mkorman9.services;

import com.github.mkorman9.models.User;

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

    public Optional<User> getById(UUID id) {
        var user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }

    public List<User> getAllUsers() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    @Transactional
    public void addUser(String name) {
        var user = new User();
        user.setName(name);
        user.setCreatedAt(Instant.now());

        entityManager.persist(user);
    }
}
