-- liquibase formatted sql

-- changeset nezuk:1742482234438-1
CREATE TABLE chats.tags
(
    id  BIGSERIAL PRIMARY KEY,
    tag VARCHAR(255) UNIQUE
);
