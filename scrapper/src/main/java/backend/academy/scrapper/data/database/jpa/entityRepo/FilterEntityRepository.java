package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.FilterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FilterEntityRepository extends JpaRepository<FilterEntity, Integer> {
    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO chats.filter_on_chat (chat_id, filter_id) VALUES (:chatId, :filterId)",
            nativeQuery = true)
    void insertFilterOnChat(@Param("chatId") Long chatId, @Param("filterId") Long filterId);
}
