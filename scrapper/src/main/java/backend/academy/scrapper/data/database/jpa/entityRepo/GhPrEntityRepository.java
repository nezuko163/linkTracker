package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.GhPrEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GhPrEntityRepository extends JpaRepository<GhPrEntity, Long> {
    List<GhPrEntity> findByLinkId(Long linkId);

    Boolean existsByLinkId(Long linkId);
}
