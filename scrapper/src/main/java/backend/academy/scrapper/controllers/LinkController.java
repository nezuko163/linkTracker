package backend.academy.scrapper.controllers;

import backend.academy.StringConstants;
import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import backend.academy.scrapper.metrics.MessagesToServiceMetrics;
import backend.academy.scrapper.services.TrackingService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(StringConstants.LINKS_V1)
@RequiredArgsConstructor
public class LinkController {
    private final Logger log = LoggerFactory.getLogger(LinkController.class);
    private final TrackingService service;
    private final MessagesToServiceMetrics metrics;


    @PostMapping
    public ResponseEntity<LinkResponse> addLink(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @NotNull @RequestBody AddLinkRequest request
    ) {
        log.info("Получение ссылок chat = " + chatId + " link - {}", request.link());
        log.info("фильтры - {}", request.filters());
        log.info("теги - {}", request.tags());
        metrics.incrementMessageCount();

        return ResponseEntity.ok(service.track(
            chatId,
            request.link(),
            request.filters() == null ? List.of() : request.filters(),
            request.tags() == null ? List.of() : request.tags()));
    }

    @GetMapping()
    public ResponseEntity<ListLinksResponse> getLinks(@Positive @RequestHeader("Tg-Chat-Id") Long chatId) {
        var res = service.getTrackingLinksByChat(chatId);
        log.info("Получение ссылок chat = " + chatId + "list - {}", res.links());
        metrics.incrementMessageCount();

        return ResponseEntity.ok(res);
    }

    @DeleteMapping()
    public ResponseEntity<LinkResponse> removeLink(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @NotNull @RequestBody RemoveLinkRequest request
    ) {
        log.info("Получение ссылок chat = " + chatId + "link - {}", request.link());
        metrics.incrementMessageCount();

        return ResponseEntity.ok(service.untrack(chatId, request.link()));
    }
}
