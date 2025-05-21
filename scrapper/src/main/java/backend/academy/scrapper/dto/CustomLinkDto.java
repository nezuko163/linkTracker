package backend.academy.scrapper.dto;

import backend.academy.scrapper.domain.model.ObservableService;
import java.time.Instant;

public record CustomLinkDto(
        Long id, String url, Integer subscribers, ObservableService service, Instant lastCheckedTime) {}
