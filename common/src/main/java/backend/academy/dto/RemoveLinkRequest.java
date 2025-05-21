package backend.academy.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RemoveLinkRequest(@NotEmpty @NotNull String link) {}
