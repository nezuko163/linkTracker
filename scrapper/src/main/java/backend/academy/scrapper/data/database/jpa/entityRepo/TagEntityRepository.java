package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TagEntityRepository extends JpaRepository<TagEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO chats.tag_on_chat (chat_id, tag_id) VALUES (:chatId, :tagId)", nativeQuery = true)
    void insertTagOnChat(@Param("chatId") Long chatId, @Param("tagId") Long tagId);
}
