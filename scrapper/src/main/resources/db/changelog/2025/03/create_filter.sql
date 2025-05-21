-- liquibase formatted sql

-- changeset nezuk:1742482171481-1
CREATE TABLE chats.filters
(
    id     BIGSERIAL PRIMARY KEY ,
    filter VARCHAR(255) UNIQUE,
    value VARCHAR(255)
);


