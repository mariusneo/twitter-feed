CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE tweets
(
    id BIGINT PRIMARY KEY NOT NULL,
    text VARCHAR(140),
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
) WITHOUT OIDS;



CREATE TABLE users
(
    id BIGINT PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    screen_name VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    profile_image_url VARCHAR(1024)
) WITHOUT OIDS;