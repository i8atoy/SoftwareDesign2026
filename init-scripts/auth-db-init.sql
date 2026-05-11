-- Runs once when the auth-db container is first created.
-- Inserts the three application roles so they are available
-- immediately after auth-service starts up.

CREATE TABLE IF NOT EXISTS roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name)
VALUES ('USER'), ('MANAGER'), ('ADMIN')
ON CONFLICT (name) DO NOTHING;
