package backend.academy.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record LinkResponse(Long id, String url, List<String> tags, List<String> filters) implements Serializable {
    public static LinkResponse of(Long id, String url) {
        return new LinkResponse(id, url, List.of(), List.of());
    }

    public static LinkResponse empty() {
        return new LinkResponse(null, null, null, null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof LinkResponse link) {
            return this.url.equals(link.url()) && this.id.equals(link.id());
        }
        return false;
    }
}
