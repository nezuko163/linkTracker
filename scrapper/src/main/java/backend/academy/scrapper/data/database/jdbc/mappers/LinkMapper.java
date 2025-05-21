package backend.academy.scrapper.data.database.jdbc.mappers;

import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.util.LinkParser;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor
@AllArgsConstructor
public class LinkMapper implements RowMapper<CustomLink> {
    private static final Logger log = LogManager.getLogger(LinkMapper.class);
    private CustomLink link;
    private Instant lastCheckedTime;
    private Integer subscribers;

    public LinkMapper(CustomLink customLink) {
        this(customLink, null, null);
    }

    @Override
    public CustomLink mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        if (link == null) link = LinkParser.parseUrl(rs.getString("url"));
        if (lastCheckedTime == null)
            link.lastCheckedDate(rs.getTimestamp("last_checked_time").toInstant());
        if (subscribers == null) link.subscribers(rs.getInt("subscribers"));
        link.id(rs.getLong("id"));

        var res = link.id(rs.getLong("id"));
        link = null;
        lastCheckedTime = null;
        subscribers = null;
        return res;
    }
}
