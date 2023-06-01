package com.github.mkorman9.core.service;

import com.github.mkorman9.security.auth.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class TestDataService {
    private static final UUID ADMIN_USER_ID = UUID.fromString("81d0d5d5-2bac-49d5-97ce-28c898f50094");
    private static final String ADMIN_USER_NAME = "admin";
    private static final String ADMIN_USER_ROLE = "ADMIN";

    @Inject
    EntityManager entityManager;

    @Transactional
    public void injectTestData() {
        if (entityManager.find(User.class, ADMIN_USER_ID) == null) {
            entityManager.createNativeQuery("INSERT INTO users(id, name, created_at) VALUES (:id, :name, NOW())")
                    .setParameter("id", ADMIN_USER_ID)
                    .setParameter("name", ADMIN_USER_NAME)
                    .executeUpdate();
            entityManager.createNativeQuery("INSERT INTO user_roles(user_id, role) VALUES (:user_id, :role)")
                    .setParameter("user_id", ADMIN_USER_ID)
                    .setParameter("role", ADMIN_USER_ROLE)
                    .executeUpdate();
        }
    }
}
