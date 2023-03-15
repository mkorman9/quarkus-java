CREATE TABLE users(
    id         UUID PRIMARY KEY,
    name       TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE SEQUENCE user_roles_id_seq;

CREATE TABLE user_roles(
    id      BIGINT DEFAULT nextval('user_roles_id_seq') PRIMARY KEY,
    user_id UUID NOT NULL,
    role    TEXT NOT NULL,

    CONSTRAINT users_user_roles_fk FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT user_roles_unique UNIQUE(user_id, role)
);
