package backend.academy.bot.services;

import backend.academy.Result;
import backend.academy.bot.cache.redis.LinksReactiveCacheService;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.RemoveLinkRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ScrapperService {
    private final ScrapperClient scrapperClient;
    private final LinksReactiveCacheService cacheService;

    public Mono<String> start(Long chatId) {
        return scrapperClient.registerChatAsync(chatId).map(res -> "Пользователь зареган");
    }

    public Mono<String> untrack(Long chatId, String url) {
        return scrapperClient
                .deleteLinkAsync(chatId, new RemoveLinkRequest(url))
                .publishOn(Schedulers.boundedElastic())
                .map(data -> {
                    if (data.status() == Result.Status.SUCCESS) {
                        log.debug("delete cache data - {}", data);
                        cacheService.removeLink(chatId, data.data()).subscribe();
                    }
                    return data;
                })
                .map(res -> {
                    if (res.status() == Result.Status.SUCCESS) {
                        return "Отслеживание ссылки отменено";
                    }
                    return "Ошибка при отмене отслеживания";
                });
    }

    public Mono<List<LinkResponse>> listLinks(Long chatId) {
        return cacheService
                .getLinks(chatId)
                .collectList()
                .flatMap(cache -> {
                    log.debug("cache - {}", cache);
                    if (!cache.isEmpty()) {
                        log.debug("listLinks: cache is not empty and return");
                        return Mono.just(cache.stream()
                                .filter(link -> link.url() != null)
                                .toList());
                    }
                    log.debug("listLinks: cache is empty");
                    return Mono.empty();
                })
                .doOnNext(data -> log.info("ЖОПА - {}", data))
                .switchIfEmpty(scrapperClient
                        .getLinksAsync(chatId)
                        .publishOn(Schedulers.boundedElastic())
                        .map(res -> {
                            log.debug("start caching");
                            if (res.isEmpty()) {
                                log.debug("cache empty links");
                                cacheService.createKeyWithEmptyValue(chatId).subscribe();
                            } else {
                                log.debug("cache links - {}", res);
                                cacheService
                                        .setLinks(chatId, res.toArray(new LinkResponse[0]))
                                        .subscribe();
                            }
                            return res;
                        }));
    }

    public Mono<String> startTracking(Long chatId, String url, List<String> tags, List<String> filters) {
        return scrapperClient
                .addLinkAsync(chatId, new AddLinkRequest(url, tags, filters))
                .publishOn(Schedulers.boundedElastic())
                .map(res -> {
                    if (res.status() == Result.Status.SUCCESS) {
                        log.debug("start cacheService.setLink");
                        cacheService.setLinks(chatId, res.data()).subscribe();
                        log.debug("end cacheService.setLink");
                    }
                    return res;
                })
                .map(data -> {
                    if (data.status() == Result.Status.SUCCESS) {
                        return "Подписка на ссылку успешна";
                    }
                    return "Ошибка при подписке на ссылку";
                });
    }

    public Mono<Void> digestTime(long chatId, String time) {
        return Mono.empty();
    }
}
