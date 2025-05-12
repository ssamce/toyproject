CREATE TABLE admin_log
(
    id              BIGINT AUTO_INCREMENT                 NOT NULL,
    user_id         BIGINT                                NOT NULL,
    action_type     ENUM ('POST', 'PUT', 'GET', 'DELETE') NOT NULL,
    api             VARCHAR(50)                           NOT NULL,
    duration        BIGINT                                NOT NULL,
    request_params  TEXT                                  NULL,
    response_params TEXT                                  NULL,
    user_agent      TEXT                                  NULL,
    remote_ip       VARCHAR(45)                           NULL,
    created_at      datetime                              NOT NULL,
    CONSTRAINT pk_admin_log PRIMARY KEY (id)
);

CREATE TABLE user_log
(
    id              BIGINT AUTO_INCREMENT                 NOT NULL,
    user_id         BIGINT                                NOT NULL,
    action_type     ENUM ('POST', 'PUT', 'GET', 'DELETE') NOT NULL,
    api             VARCHAR(50)                           NOT NULL,
    duration        BIGINT                                NOT NULL,
    request_params  TEXT                                  NULL,
    response_params TEXT                                  NULL,
    user_agent      TEXT                                  NULL,
    remote_ip       VARCHAR(45)                           NULL,
    created_at      datetime                              NOT NULL,
    CONSTRAINT pk_user_log PRIMARY KEY (id)
);

ALTER TABLE admin_log
    ADD CONSTRAINT FK_ADMIN_LOG_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_admin_log_user_id ON admin_log (user_id);

ALTER TABLE user_log
    ADD CONSTRAINT FK_USER_LOG_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_user_log_user_id ON user_log (user_id);