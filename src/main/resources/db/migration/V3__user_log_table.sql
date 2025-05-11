CREATE TABLE user_log
(
    id              BIGINT AUTO_INCREMENT  NOT NULL,
    user_id         BIGINT                 NOT NULL,
    action_type     ENUM ('USER', 'ADMIN') NOT NULL,
    type            VARCHAR(50)            NOT NULL,
    request_params  TEXT                   NULL,
    response_params TEXT                   NULL,
    user_agent      TEXT                   NULL,
    remote_ip       VARCHAR(45)            NULL,
    created_at      datetime               NOT NULL,
    CONSTRAINT pk_user_log PRIMARY KEY (id)
);