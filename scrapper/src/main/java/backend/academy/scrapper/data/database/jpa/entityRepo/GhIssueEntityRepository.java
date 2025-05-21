package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.GhIssueEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GhIssueEntityRepository extends JpaRepository<GhIssueEntity, Long> {
    List<GhIssueEntity> findByLinkId(Long linkId);

    Boolean existsByLinkId(Long linkId);
}
