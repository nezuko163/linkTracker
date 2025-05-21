package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.StackOverflowAnswerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SOAnswerEntityRepo extends JpaRepository<StackOverflowAnswerEntity, Long> {
    List<StackOverflowAnswerEntity> findByLinkId(Long stackOverflowEntityId);

    Boolean existsByLinkId(Long linkId);
}
