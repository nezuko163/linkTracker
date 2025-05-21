package backend.academy.scrapper.data.repository.chats.tgChatStorage.impl;

import backend.academy.scrapper.data.repository.chats.tgChatStorage.TgChatStorageRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class TgChatStorageJdbc implements TgChatStorageRepository {
    private final JdbcClient jdbcClient;

    @Override
    public boolean registerChat(Long chatId) {
        var rows = jdbcClient
                .sql("INSERT INTO tg_chats (chat_id) VALUES (:chat_id) ON CONFLICT (chat_id) DO NOTHING")
                .param("chat_id", chatId)
                .update();
        return rows == 1;
    }

    @Override
    public boolean removeChat(Long chatId) {
        var rows = jdbcClient
                .sql("DELETE FROM tg_chats WHERE chat_id = :chat_id")
                .param("chat_id", chatId)
                .update();

        return rows == 1;
    }

    @Override
    public boolean isChatRegistered(Long chatId) {
        try {
            jdbcClient
                    .sql("SELECT chat_id FROM tg_chats WHERE chat_id = :chat_id")
                    .param("chat_id", chatId)
                    .query(Long.class)
                    .single();
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Set<Long> registeredChats() {
        return new HashSet<>(
                jdbcClient.sql("SELECT chat_id FROM tg_chats").query(Long.class).list());
    }

    @Override
    public void clear() {
        jdbcClient.sql("TRUNCATE TABLE tg_chats CASCADE").update();
    }
}
