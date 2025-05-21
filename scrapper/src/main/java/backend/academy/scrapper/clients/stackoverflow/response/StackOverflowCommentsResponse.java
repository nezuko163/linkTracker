package backend.academy.scrapper.clients.stackoverflow.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverflowCommentsResponse(
        List<StackOverflowComment> items, @JsonProperty("has_more") boolean hasMore) {}
