package backend.academy.scrapper.data.repository.links.timeManagment.impl;

import backend.academy.scrapper.data.repository.links.timeManagment.LinkTimeRepository;
import backend.academy.scrapper.domain.model.CustomLink;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class LinkTimeRepositoryJdbc implements LinkTimeRepository {
    private final JdbcClient jdbcClient;

    @Override
    public CustomLink updateLastCheckedTime(Instant time, CustomLink link) {
        jdbcClient
                .sql("UPDATE links.links SET last_checked_time = :time WHERE id = :linkId")
                .param("time", Timestamp.from(time))
                .param("linkId", link.id())
                .update();

        return link.lastCheckedDate(time);
    }
}
