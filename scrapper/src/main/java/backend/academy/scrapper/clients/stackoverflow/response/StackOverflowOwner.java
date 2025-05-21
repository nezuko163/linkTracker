package backend.academy.scrapper.clients.stackoverflow.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StackOverflowOwner(@JsonProperty("display_name") String displayName, String link) {}
