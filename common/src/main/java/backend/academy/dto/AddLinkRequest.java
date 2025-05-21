package backend.academy.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AddLinkRequest(@NotEmpty @NotNull String link, List<String> tags, List<String> filters) {}
