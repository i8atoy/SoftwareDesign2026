-- Runs once when the auth-db container is first created.

CREATE TABLE IF NOT EXISTS roles (
                                     id   BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
                                     id              BIGSERIAL PRIMARY KEY,
                                     user_name       VARCHAR(100) NOT NULL UNIQUE,
                                     password        VARCHAR(255) NOT NULL,
                                     managed_team_id BIGINT
);

CREATE TABLE IF NOT EXISTS users_roles (
                                           user_id BIGINT REFERENCES users(id),
                                           role_id BIGINT REFERENCES roles(id),
                                           PRIMARY KEY (user_id, role_id)
);

INSERT INTO roles (name) VALUES ('USER'), ('MANAGER'), ('ADMIN')
ON CONFLICT (name) DO NOTHING;

-- password is "password123" for all users
INSERT INTO users (user_name, password, managed_team_id) VALUES
                                                             ('admin',    '$2a$10$f0iezdsfEtJz/aCiVVv3d.bG1jgaoA6M8BpxRmh7ifahi1/jxCckq', NULL),
                                                             ('manager1', '$2a$10$f0iezdsfEtJz/aCiVVv3d.bG1jgaoA6M8BpxRmh7ifahi1/jxCckq', 1),
                                                             ('manager2', '$2a$10$f0iezdsfEtJz/aCiVVv3d.bG1jgaoA6M8BpxRmh7ifahi1/jxCckq', 2),
                                                             ('user1',    '$2a$10$f0iezdsfEtJz/aCiVVv3d.bG1jgaoA6M8BpxRmh7ifahi1/jxCckq', NULL)
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'admin'    AND r.name = 'ADMIN'   ON CONFLICT DO NOTHING;
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'manager1' AND r.name = 'MANAGER' ON CONFLICT DO NOTHING;
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'manager2' AND r.name = 'MANAGER' ON CONFLICT DO NOTHING;
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'user1'    AND r.name = 'USER'    ON CONFLICT DO NOTHING;