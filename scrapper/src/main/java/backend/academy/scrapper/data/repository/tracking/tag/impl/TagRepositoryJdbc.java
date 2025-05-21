package backend.academy.scrapper.data.repository.tracking.tag.impl;

import backend.academy.model.TagModel;
import backend.academy.scrapper.data.repository.tracking.tag.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class TagRepositoryJdbc implements TagRepository {
    private final JdbcClient jdbcClient;

    @Override
    public List<TagModel> getAll() {
        return jdbcClient
                .sql("""
                SELECT * FROM chats.tags
                """)
                .query((rs, rowNum) -> new TagModel(rs.getLong(1), rs.getString(2)))
                .list();
    }

    @Override
    public List<TagModel> saveTags(List<String> tags) {
        var res = new ArrayList<TagModel>();
        tags.forEach(tag -> {
            var id = jdbcClient
                    .sql(
                            """
                    INSERT INTO chats.tags
                    (tag) VALUES (:tag)
                    ON CONFLICT (tag)
                    DO UPDATE SET tag = excluded.tag
                    RETURNING id
                    """)
                    .param("tag", tag)
                    .query(Long.class)
                    .single();
            res.add(new TagModel(id, tag));
        });
        return res;
    }
}
