package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.model.FilterModel;
import backend.academy.model.TagModel;
import backend.academy.scrapper.data.database.jpa.entities.ChatEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LinksWithChatsEntityRepository extends JpaRepository<ChatEntity, Long> {
    ChatEntity findByChatIdAndLinkUrlIgnoreCase(Long chatId, String link);

    ChatEntity findByChatIdAndLinkId(Long chatId, Long linkId);

    Set<ChatEntity> findAllByChatId(Long chatId);

    @Modifying
    Integer deleteByChatIdAndLinkUrlIgnoreCase(Long chatId, String url);

    List<ChatEntity> findByLinkId(Long id);

    @Query(
            value =
                    """
        SELECT filters.id, filter, value FROM chats.filters
        JOIN chats.filter_on_chat foc on filters.id = foc.filter_id
        WHERE foc.chat_id = :chatId;
        """,
            nativeQuery = true)
    List<FilterModel> getFiltersByChatId(@Param("chatId") Long chatId);

    @Query(
            value =
                    """
        SELECT tags.id, tags.tag FROM chats.tags
        JOIN chats.filter_on_chat foc on tags.id = foc.filter_id
        WHERE foc.chat_id = :chatId;
        """,
            nativeQuery = true)
    List<TagModel> getTagsByChatId(@Param("chatId") Long chatId);

    void deleteByChatIdAndLinkId(Long chatId, Long linkId);

    void deleteAllByChatId(long tgChatId);
}
