CREATE TABLE IF NOT EXISTS request
(
    request_id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    users_id    BIGINT                                  NOT NULL,
    description VARCHAR(255)                            NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (request_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name_user VARCHAR(10)                             NOT NULL,
    email     VARCHAR(20)                             NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name_item   VARCHAR(20)                             NOT NULL,
    description VARCHAR(50)                             NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner       BIGINT                                  NOT NULL,
    request     BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (item_id),
    CONSTRAINT fk_items_users FOREIGN KEY (owner) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_item_item_request FOREIGN KEY (request) REFERENCES request (request_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking
(
    booking_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_booking  TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    finish_booking TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    items_id       BIGINT                                  NOT NULL,
    users_id       BIGINT                                  NOT NULL,
    status         VARCHAR(20)                             NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (booking_id),
    CONSTRAINT fk_booking_item FOREIGN KEY (items_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_user FOREIGN KEY (users_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text       VARCHAR(255)                            NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    user_id    BIGINT                                  NOT NULL,
    created    TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (comment_id),
    CONSTRAINT fk_comments_item FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);