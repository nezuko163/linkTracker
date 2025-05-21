package backend.academy.scrapper.data.repository.tracking.tracking.impl;

import backend.academy.StringConstants;
import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.data.repository.tracking.tracking.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrackingRepositoryJdbc implements TrackingRepository {
    private static final Logger log = LogManager.getLogger(TrackingRepositoryJdbc.class);
    private final JdbcClient jdbcClient;
    private final LinkStorageRepository linkStorageRepository;

    // возвращает айди отношения
    @Override
    public Long saveLinkOnChat(Long linkId, Long tgChatId) {
        return jdbcClient
                .sql(
                        """
                INSERT INTO chats.chats
                (tg_chat_id, link_id) VALUES(:tgChatId, :linkId)
                RETURNING id
                """)
                .param("tgChatId", tgChatId)
                .param("linkId", linkId)
                .query(Long.class)
                .single();
    }

    @Override
    public void saveTagOnRelation(Long relationId, Long tagId) {
        jdbcClient
                .sql(
                        """
                INSERT INTO chats.tag_on_chat
                (chat_id, tag_id) VALUES(:chatId, :tagId)
                """)
                .param("chatId", relationId)
                .param("tagId", tagId)
                .update();
    }

    @Override
    public void saveFilterOnRelation(Long relationId, Long filterId) {
        jdbcClient
                .sql(
                        """
                        INSERT INTO chats.filter_on_chat
                        (chat_id, filter_id) VALUES(:chatId, :filterId)
                        """)
                .param("chatId", relationId)
                .param("filterId", filterId)
                .update();
    }

    @Override
    public void removeRelation(Long linkId, Long tgChatId) {
        try {
            jdbcClient
                    .sql(
                            """
                        DELETE FROM chats.chats
                        WHERE link_id = :linkId AND tg_chat_id = :tgChatId
                        RETURNING id
                        """)
                    .param("linkId", linkId)
                    .param("tgChatId", tgChatId)
                    .query(Long.class)
                    .single();
        } catch (EmptyResultDataAccessException empty) {
            throw new NotFoundError(StringConstants.cantFindLinkInChat("id = " + linkId, tgChatId));
        }
    }

    @Override
    public void removeRelation(String url, Long tgChatId) {
        removeRelation(linkStorageRepository.findIdByUrl(url), tgChatId);
    }

    @Override
    public void removeAllRelationsFromChat(Long tgChatId) {
        jdbcClient
                .sql(
                        """
                DELETE FROM chats.chats
                WHERE tg_chat_id = :tgChatId
                """)
                .param("tgChatId", tgChatId)
                .update();
    }
}
