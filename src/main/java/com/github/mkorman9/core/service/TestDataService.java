package com.github.mkorman9.core.service;

import com.github.mkorman9.security.auth.model.User;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class TestDataService {
    private static final UUID ADMIN_USER_ID = UUID.fromString("81d0d5d5-2bac-49d5-97ce-28c898f50094");
    private static final String ADMIN_USER_NAME = "admin";
    private static final String ADMIN_USER_ROLE = "ADMIN";

    @Inject
    Mutiny.SessionFactory sessionFactory;

    public void injectTestData() {
        sessionFactory
                .withTransaction((session) -> {
                    return session.find(User.class, ADMIN_USER_ID)
                            .onItem().transformToUni(user -> {
                                if (user != null) {
                                    return Uni.createFrom().nullItem().replaceWithVoid();
                                }

                                return session.createNativeQuery("INSERT INTO users(id, name, created_at) VALUES (:id, :name, NOW())")
                                        .setParameter("id", ADMIN_USER_ID)
                                        .setParameter("name", ADMIN_USER_NAME)
                                        .executeUpdate()
                                        .flatMap(v -> {
                                            return session.createNativeQuery("INSERT INTO user_roles(user_id, role) VALUES (:user_id, :role)")
                                                    .setParameter("user_id", ADMIN_USER_ID)
                                                    .setParameter("role", ADMIN_USER_ROLE)
                                                    .executeUpdate();
                                        });
                            });
                })
                .subscribe().with(v -> {}, e -> {});
    }
}
