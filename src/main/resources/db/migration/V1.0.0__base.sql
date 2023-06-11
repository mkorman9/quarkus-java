CREATE TABLE users (
    id         UUID PRIMARY KEY,
    name       TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role    TEXT NOT NULL,

    CONSTRAINT pk_user_roles UNIQUE(user_id, role),
    CONSTRAINT fk_user_roles_users FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE tokens (
    token          TEXT PRIMARY KEY,
    user_id        UUID NOT NULL,
    issued_at      TIMESTAMP NOT NULL,
    remote_address TEXT NOT NULL,
    device         TEXT NOT NULL,
    valid          BOOLEAN NOT NULL,

    CONSTRAINT fk_tokens_users FOREIGN KEY(user_id) REFERENCES users(id)
);
