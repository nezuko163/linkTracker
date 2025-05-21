package backend.academy.scrapper.data.repository.chats.chatStorage.impl;

import backend.academy.scrapper.data.repository.chats.chatStorage.ChatStorageRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class ChatStorageRepositoryJdbc implements ChatStorageRepository {
    private final JdbcClient jdbcClient;
    private final LinkStorageRepository linkStorageRepository;

    @Override
    public List<Long> getTgChatsTrackingLink(String url) {
        return getTgChatsTrackingLink(linkStorageRepository.findIdByUrl(url));
    }

    @Override
    public List<Long> getTgChatsTrackingLink(Long linkId) {
        return jdbcClient
                .sql(
                        """
                SELECT chats.chats.tg_chat_id FROM chats.chats
                INNER JOIN links.links l on chats.link_id = l.id
                WHERE l.id = :linkId;
                """)
                .param("linkId", linkId)
                .query((rs, rowNum) -> rs.getLong("tg_chat_id"))
                .list();
    }

    @Override
    public boolean isChatTrackingLink(Long linkId, Long tgChatId) {
        return jdbcClient
                .sql(
                        """
                SELECT COUNT(*) > 0
                FROM chats.chats
                WHERE tg_chat_id = :tgChatId AND link_id = :linkId
                """)
                .param("tgChatId", tgChatId)
                .param("linkId", linkId)
                .query(Boolean.class)
                .single();
    }
}
