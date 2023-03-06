CREATE TABLE users(
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE user_roles(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    role TEXT NOT NULL,
    CONSTRAINT users_user_roles_fk FOREIGN KEY(user_id) REFERENCES users(id)
);
