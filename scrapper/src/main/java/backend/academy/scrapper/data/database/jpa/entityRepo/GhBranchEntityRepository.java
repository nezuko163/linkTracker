package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.GhBranchEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GhBranchEntityRepository extends JpaRepository<GhBranchEntity, String> {
    List<GhBranchEntity> findByLinkId(Long linkId);

    Boolean existsByLinkId(Long linkId);
}
