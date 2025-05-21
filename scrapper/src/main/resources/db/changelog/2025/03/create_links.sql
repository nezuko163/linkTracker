-- liquibase formatted sql

-- changeset nezuk:1742302658131-1
CREATE TABLE links.links
(
    id                BIGSERIAL PRIMARY KEY,
    url               VARCHAR(255) UNIQUE,
    last_checked_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    subscribers       INTEGER                     DEFAULT 0,
    service           VARCHAR(50)                 DEFAULT 'NONE'
);

