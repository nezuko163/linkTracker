package backend.academy.scrapper.data.database.jpa.entityRepo;

import backend.academy.scrapper.data.database.jpa.entities.TgChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEntityRepository extends JpaRepository<TgChatEntity, Long> {}
