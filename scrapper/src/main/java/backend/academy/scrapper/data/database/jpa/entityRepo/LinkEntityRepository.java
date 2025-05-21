package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.LinkEntity;
import java.sql.Timestamp;
import backend.academy.scrapper.domain.model.ObservableService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LinkEntityRepository extends JpaRepository<LinkEntity, Long> {
    @Transactional
    LinkEntity getLinkEntityByUrlLikeIgnoreCase(String url);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE links.links CASCADE", nativeQuery = true)
    void truncate();

    @Modifying
    @Query(value = "UPDATE links.links SET last_checked_time = :time WHERE id = :linkId", nativeQuery = true)
    @Transactional
    void update(@Param("time") Timestamp time, @Param("linkId") Long linkId);

    @Modifying
    @Query(value = "UPDATE links.links SET subscribers = subscribers + 1 WHERE LOWER(url) = :url", nativeQuery = true)
    @Transactional
    void incrementSubscriber(@Param("url") String url);

    @Modifying
    @Query(value = "UPDATE links.links SET subscribers = subscribers - 1 WHERE LOWER(url) = :url", nativeQuery = true)
    @Transactional
    void decrementSubscriber(@Param("url") String url);

    long countByService(ObservableService service);

    @NotNull
    Page<LinkEntity> findAll(@NotNull Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM links.links", nativeQuery = true)
    long count();
}
