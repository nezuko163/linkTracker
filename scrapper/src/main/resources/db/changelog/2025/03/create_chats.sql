-- liquibase formatted sql

-- changeset nezuk:1742302673795-1
CREATE TABLE chats.chats
(
    id      BIGSERIAL PRIMARY KEY ,
    tg_chat_id BIGINT,
    link_id BIGINT,
    FOREIGN KEY (link_id) REFERENCES links.links(id) ON DELETE CASCADE,
    CONSTRAINT unique_chat_link UNIQUE (tg_chat_id, link_id)
);
