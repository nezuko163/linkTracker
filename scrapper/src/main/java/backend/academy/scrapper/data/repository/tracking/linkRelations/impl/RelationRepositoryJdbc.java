package backend.academy.scrapper.data.repository.tracking.linkRelations.impl;

import backend.academy.dto.LinkResponse;
import backend.academy.model.FilterModel;
import backend.academy.model.TagModel;
import backend.academy.scrapper.data.repository.tracking.filter.FilterRepository;
import backend.academy.scrapper.data.repository.tracking.linkRelations.RelationRepository;
import backend.academy.scrapper.data.repository.tracking.tag.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class RelationRepositoryJdbc implements RelationRepository {

    private final JdbcClient jdbcClient;
    private final FilterRepository filterRepository;
    private final TagRepository tagRepository;

    @Override
    public Long getRelationId(Long linkId, Long tgChatId) {
        return jdbcClient
                .sql(
                        """
                SELECT id FROM chats.chats
                WHERE tg_chat_id = :tgChatId AND link_id = :linkId;
                """)
                .param("tgChatId", tgChatId)
                .param("linkId", linkId)
                .query((rs, rowNum) -> rs.getLong(1))
                .single();
    }

    @Override
    public List<FilterModel> getFiltersByChatIdAndLinkId(Long tgChatId, Long linkId) {
        try {
            return jdbcClient
                    .sql(
                            """
                    SELECT filters.id, filters.filter, filters.value FROM chats.filters
                    JOIN chats.filter_on_chat foc on filters.id = foc.filter_id
                    WHERE chat_id = :chatId
                    """)
                    .param("chatId", getRelationId(linkId, tgChatId))
                    .query((rs, rowNum) -> new FilterModel(rs.getLong(1), rs.getString(2), rs.getString(3)))
                    .list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<TagModel> getTagsByChatIdAndLinkId(Long tgChatId, Long linkId) {
        try {
            return jdbcClient
                    .sql(
                            """
                    SELECT tags.tag, tags.id FROM chats.tags
                    JOIN chats.tag_on_chat foc on tags.id = foc.tag_id
                    WHERE chat_id = :chatId
                    """)
                    .param("chatId", getRelationId(linkId, tgChatId))
                    .query((rs, rowNum) -> new TagModel(rs.getLong(2), rs.getString(1)))
                    .list();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<LinkResponse> getTrackedLinksByChat(Long tgChatId) {
        return jdbcClient
                .sql(
                        """
                SELECT links.id, links.url
                FROM links.links
                INNER JOIN chats.chats ON links.id = chats.chats.link_id
                WHERE tg_chat_id = :tgChatId
                """)
                .param("tgChatId", tgChatId)
                .query((rs, rowNum) -> LinkResponse.of(rs.getLong(1), rs.getString(2)))
                .stream()
                .map(link -> new LinkResponse(
                        link.id(),
                        link.url(),
                        getTagsByChatIdAndLinkId(tgChatId, link.id()).stream()
                                .map(TagModel::toString)
                                .toList(),
                        getFiltersByChatIdAndLinkId(tgChatId, link.id()).stream()
                                .map(FilterModel::toString)
                                .toList()))
                .toList();
    }

    @Override
    public void __clear_forTest() {
        jdbcClient
                .sql(
                        """
                TRUNCATE TABLE links.links CASCADE;
                TRUNCATE TABLE chats.filters CASCADE;
                TRUNCATE TABLE chats.tags CASCADE;
                """)
                .update();
    }
}
