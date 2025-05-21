-- liquibase formatted sql
create table chats.tag_on_chat
(
    id        BIGSERIAL PRIMARY KEY,
    chat_id   BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats.chats(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES chats.tags(id) ON DELETE CASCADE
);

alter table chats.tag_on_chat
    add constraint tag_on_chat_pk_2
        unique (chat_id, tag_id);



