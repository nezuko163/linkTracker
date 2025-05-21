package backend.academy.scrapper.data.repository.links.LinkStorage.impl;

import backend.academy.Paginated;
import backend.academy.StringConstants;
import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.database.jdbc.mappers.LinkMapper;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.ObservableService;
import backend.academy.scrapper.properties.ScrapperProperties;
import backend.academy.scrapper.util.LinkParser;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class LinkStorageRepositoryJdbc implements LinkStorageRepository {
    private final ScrapperProperties config;
    private static final Logger log = LogManager.getLogger(LinkStorageRepositoryJdbc.class);
    private final JdbcClient jdbcClient;

    @Override
    public CustomLink saveLink(String url) {
        log.debug("Saving link: {}", url);
        var link = LinkParser.parseUrl(url);
        log.debug("Saving link: parsedl link - {}", link);
        var id = jdbcClient
            .sql("INSERT INTO links.links (url, last_checked_time, subscribers, service) "
                + "VALUES (:url, :last_checked_time, :subscribers, :service) "
                + "RETURNING id")
            .param("url", url)
            .param("last_checked_time", Timestamp.from(Instant.now()))
            .param("subscribers", 0)
            .param("service", link.service().name())
            .query(Long.class)
            .single();

        log.debug("Link saved: {}", link.id(id));

        return link;
    }

    @Override
    public CustomLink findLinkByUrl(String url) {
        log.debug("findLinkByUrl: {}", url);
        var link = LinkParser.parseUrl(url);
        log.debug("findLinkByUrl: parsed link - {}", link);
        try {
            var res = jdbcClient
                .sql("SELECT id, last_checked_time, subscribers from links.links where LOWER(url) = LOWER(:url)")
                .param("url", url)
                .query(new LinkMapper(link))
                .single();
            log.debug("Link found: {}", res);
            return res;
        } catch (Exception e) {
            throw new NotFoundError(StringConstants.cantFindLink(url));
        }
    }

    @Override
    public Set<CustomLink> __findAll_forTest() {
        var a = jdbcClient
            .sql("SELECT * FROM links.links")
            .query(new LinkMapper())
            .list();
        log.debug("__findAll_forTest: links - {}", a);
        return new HashSet<>(a);
    }

    @Override
    public Paginated<CustomLink> findAllPaging(int page) {
        var count = jdbcClient
            .sql("SELECT COUNT(*) FROM links.links")
            .query((rs, rowNum) -> rs.getLong(1))
            .single();

        var res = jdbcClient
            .sql(
                """
                    SELECT id, url, subscribers, last_checked_time FROM links.links
                    WHERE id <= :page
                    ORDER BY id DESC
                    LIMIT :pageSize""")
            .param("page", (page + 1) * config.itemsOnPage())
            .param("pageSize", config.itemsOnPage())
            .query(new LinkMapper())
            .list();
        log.debug("findAllPaging (page = {}): links - {}", page, res);

        return new Paginated<>(
            page,
            count > (((long) page * config.itemsOnPage()) + res.size()),
            res,
            pagesCount(count, config.itemsOnPage()));
    }

    @Override
    public Long findIdByUrl(String url) {
        try {
            var id = jdbcClient
                .sql("SELECT id from links.links where LOWER(url) = LOWER(:url)")
                .param("url", url)
                .query(Long.class)
                .single();
            log.debug("findIdByUrl: id = {}", id);
            return id;
        } catch (Exception e) {
            throw new NotFoundError(StringConstants.cantFindLink(url));
        }
    }

    @Override
    public void clear() {
        jdbcClient
            .sql("""
                TRUNCATE TABLE links.links CASCADE
                """)
            .update();
    }

    @Override
    public int pagesCount() {
        return pagesCount(
            jdbcClient
                .sql("""
                    SELECT COUNT(*) FROM links.links
                    """)
                .query((rs, rowNum) -> rs.getLong(1))
                .single(),
            config.itemsOnPage());
    }

    @Override
    public long countLinksByService(ObservableService service) {
        return jdbcClient
            .sql("""
                SELECT COUNT(*) FROM links.links
                WHERE service = :service
                """)
            .param("service", service.name())
            .query(Long.class)
            .single();
    }
}
