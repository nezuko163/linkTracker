package backend.academy.scrapper.data.repository.links.timeManagment.impl;

import backend.academy.scrapper.data.database.jpa.entityRepo.LinkEntityRepository;
import backend.academy.scrapper.data.repository.links.timeManagment.LinkTimeRepository;
import backend.academy.scrapper.domain.model.CustomLink;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class LinkTimeRepositoryJpa implements LinkTimeRepository {
    private final LinkEntityRepository linkEntityRepository;

    @Override
    public CustomLink updateLastCheckedTime(Instant time, CustomLink link) {
        linkEntityRepository.update(Timestamp.from(time), link.id());
        return link.lastCheckedDate(time);
    }
}
