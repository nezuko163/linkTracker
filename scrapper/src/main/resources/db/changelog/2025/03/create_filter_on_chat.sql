-- liquibase formatted sql

create table chats.filter_on_chat
(
    id        BIGSERIAL PRIMARY KEY,
    chat_id   BIGINT NOT NULL,
    filter_id BIGINT NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats.chats(id) ON DELETE CASCADE,
    FOREIGN KEY (filter_id) REFERENCES chats.filters(id) ON DELETE CASCADE
);

alter table chats.filter_on_chat
    add constraint filter_on_chat_pk_2
        unique (chat_id, filter_id);

