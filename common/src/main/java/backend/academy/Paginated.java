package backend.academy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record Paginated<T>(int page, boolean hasNext, List<T> data, int pagesCount) implements Iterable<T> {
    public static <T> Paginated<T> empty() {
        return new Paginated<>(0, false, new ArrayList<>(), 0);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return data.iterator();
    }
}
