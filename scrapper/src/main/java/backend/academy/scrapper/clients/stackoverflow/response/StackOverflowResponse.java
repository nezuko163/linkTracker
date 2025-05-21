package backend.academy.scrapper.clients.stackoverflow.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverflowResponse(List<StackOverflowQuestion> items, @JsonProperty("has_more") boolean hasMore) {}
