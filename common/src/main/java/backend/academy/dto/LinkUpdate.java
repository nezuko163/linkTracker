package backend.academy.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LinkUpdate(Long id, @NotEmpty String url, @NotEmpty String description, @NotEmpty List<Long> tgChatIds) {}
