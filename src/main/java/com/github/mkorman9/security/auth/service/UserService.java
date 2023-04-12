package com.github.mkorman9.security.auth.service;

import com.github.mkorman9.security.auth.dto.UserEvent;
import com.github.mkorman9.security.auth.exception.RoleAlreadyAssignedException;
import com.github.mkorman9.security.auth.exception.UserNotFoundException;
import com.github.mkorman9.security.auth.model.User;
import com.github.mkorman9.security.auth.model.UserRole;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {
    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Inject
    EventBus eventBus;

    public Uni<User> getById(UUID id) {
        return sessionFactory.withTransaction(session -> {
            return session.find(User.class, id);
        });
    }

    public Uni<List<User>> getAllUsers() {
        return sessionFactory.withTransaction(session -> {
            return session.createQuery("from User", User.class).getResultList();
        });
    }

    public Uni<User> addUser(String name) {
        var user = new User();
        user.setName(name);
        user.setCreatedAt(Instant.now());

        return sessionFactory
                .withTransaction(session -> {
                    return session.persist(user);
                })
                .map(v -> user)
                .onItem().invoke(() -> {
                    eventBus.publish(
                            UserEvent.TOPIC_NAME,
                            new UserEvent(user.getId(), Instant.now(), UserEvent.EventType.CREATED)
                    );
                });
    }

    public Uni<Void> assignRole(UUID id, String role) {
        return getById(id)
                .onItem().ifNull().failWith(new UserNotFoundException())
                .onItem().ifNotNull()
                .transformToUni(user -> {
                    var roleEntity = new UserRole();
                    roleEntity.setRole(role);
                    user.getRoles().add(roleEntity);

                    return sessionFactory.withTransaction(session -> {
                        return session.merge(user)
                                .flatMap(v -> session.flush());
                    });
                })
                .onFailure()
                .transform(e -> {
                    if (e.getCause() instanceof ConstraintViolationException violation) {
                        if (violation.getConstraintName().equals(UserRole.UNIQUE_CONSTRAINT)) {
                            return new RoleAlreadyAssignedException();
                        }
                    }

                    return e;
                });
    }
}
