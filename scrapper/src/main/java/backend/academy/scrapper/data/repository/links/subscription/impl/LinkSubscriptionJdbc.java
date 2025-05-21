package backend.academy.scrapper.data.repository.links.subscription.impl;

import backend.academy.StringConstants;
import backend.academy.dto.LinkResponse;
import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.data.repository.links.subscription.LinkSubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class LinkSubscriptionJdbc implements LinkSubscriptionRepository {
    private static final Logger log = LogManager.getLogger(LinkSubscriptionJdbc.class);
    private final LinkStorageRepository linkStorageRepository;
    private final JdbcClient jdbcClient;

    @Override
    public LinkResponse addSubscriber(String url) {
        var id = linkStorageRepository.findIdByUrl(url);
        return addSubscriber(id, url);
    }

    @Override
    public LinkResponse addSubscriber(Long id, String url) {
        log.debug("addSubscriber: id {}, url - {}. Add subscriber url - {}", id, url, url);

        var row = jdbcClient
                .sql(
                        """
            UPDATE links.links
            SET subscribers = subscribers + 1
            WHERE id = :id
            """)
                .param("id", id)
                .update();
        if (row != 1) {
            throw new NotFoundError(StringConstants.cantFindLink(url));
        }
        return LinkResponse.of(id, url);
    }

    @Override
    public Boolean removeSubscriber(String link) {
        log.debug("removeSubscriber: link - {}", link);
        List<Long> subscribers;
        try {
            subscribers = jdbcClient
                    .sql(
                            """
                    UPDATE links.links
                    SET subscribers = subscribers - 1
                    WHERE LOWER(url) = LOWER(:url)
                    RETURNING id, subscribers;
                    """)
                    .param("url", link)
                    .query((rs, rowNum) -> List.of(rs.getLong("id"), rs.getLong("subscribers")))
                    .single();
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundError(StringConstants.cantFindLink(link));
        }

        if (subscribers.get(1) <= 0) {
            var rows = jdbcClient
                    .sql(
                            """
                    DELETE FROM links.links
                    WHERE id = :id
                    """)
                    .param("id", subscribers.getFirst())
                    .update();
            return rows == 1;
        }

        return true;
    }

    @Override
    public Boolean removeSubscriber(Long id) {
        log.debug("removeSubscriber: id - {}", id);
        long subscribers;
        try {
            subscribers = jdbcClient
                    .sql(
                            """
                    UPDATE links.links
                    SET subscribers = subscribers - 1
                    WHERE id = :id
                    RETURNING subscribers;
                    """)
                    .param("id", id)
                    .query((rs, rowNum) -> rs.getLong(1))
                    .single();
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundError(StringConstants.cantFindLink("с id = " + id));
        }

        if (subscribers <= 0) {
            var rows = jdbcClient
                    .sql(
                            """
                    DELETE FROM links.links
                    WHERE id = :id
                    """)
                    .param("id", id)
                    .update();
            return rows == 1;
        }

        return true;
    }
}
