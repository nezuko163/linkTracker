package backend.academy.scrapper.clients.stackoverflow.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverflowAnswersResponse(
        List<StackOverflowAnswer> items, @JsonProperty("has_more") boolean hasMore) {}
