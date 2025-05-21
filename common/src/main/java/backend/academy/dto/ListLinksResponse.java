package backend.academy.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {
    public static ListLinksResponse empty() {
        return new ListLinksResponse(List.of(), 0);
    }
}
