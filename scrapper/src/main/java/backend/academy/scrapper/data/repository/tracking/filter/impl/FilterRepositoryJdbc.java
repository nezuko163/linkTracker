package backend.academy.scrapper.data.repository.tracking.filter.impl;

import backend.academy.model.FilterModel;
import backend.academy.scrapper.data.repository.tracking.filter.FilterRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class FilterRepositoryJdbc implements FilterRepository {
    private final JdbcClient jdbcClient;

    @Override
    public List<FilterModel> getAll() {
        return jdbcClient
                .sql("""
                SELECT * FROM chats.filters
                """)
                .query((rs, rowNum) -> new FilterModel(rs.getLong(1), rs.getString(2), ""))
                .list();
    }

    @Override
    public List<FilterModel> saveFilters(List<FilterModel> filters) {
        var res = new ArrayList<FilterModel>();
        filters.forEach(filter -> {
            var id = jdbcClient
                    .sql(
                            """
                        INSERT INTO chats.filters
                        (filter, value) VALUES (:filter, :value)
                        ON CONFLICT (filter)
                        DO UPDATE SET filter = excluded.filter
                        RETURNING id
                        """)
                    .param("filter", filter.filter())
                    .param("value", filter.value())
                    .query(Long.class)
                    .single();
            res.add(new FilterModel(id, filter.filter(), filter.value()));
        });
        return res;
    }
}
