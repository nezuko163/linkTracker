package backend.academy.scrapper.data.database.jpa.mapper;

import backend.academy.Paginated;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public final class PageMapper {
    public static <T, Y> Paginated<Y> fromPage(Page<T> page, Function<T, Y> func) {
        return new Paginated<>(
                page.getNumber(),
                page.hasNext(),
                page.getContent().stream().map(func).toList(),
                page.getTotalPages());
    }
}
