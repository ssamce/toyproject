CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    password      VARCHAR(100) NOT NULL,
    name          VARCHAR(10)  NOT NULL,
    `role`        ENUM ('USER', 'ADMIN') NOT NULL,
    access_token  TEXT NULL,
    refresh_token TEXT NULL,
    created_at    datetime     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);