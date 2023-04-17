CREATE TABLE users (
    id         UUID PRIMARY KEY,
    name       TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE user_roles (
    id      BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    role    TEXT NOT NULL,

    CONSTRAINT user_roles_users_fk FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT user_roles_unique UNIQUE(user_id, role)
);

CREATE TABLE tokens (
    token          TEXT PRIMARY KEY,
    user_id        UUID NOT NULL,
    issued_at      TIMESTAMP NOT NULL,
    remote_address TEXT NOT NULL,
    device         TEXT NOT NULL,

    valid     BOOLEAN NOT NULL,

    CONSTRAINT tokens_users_fk FOREIGN KEY(user_id) REFERENCES users(id)
);
