package backend.academy.bot.cache.redis;

import backend.academy.dto.LinkResponse;
import java.time.Duration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LinksReactiveCacheService {
    private static final Logger log = LogManager.getLogger(LinksReactiveCacheService.class);
    private final ReactiveRedisTemplate<String, LinkResponse> reactiveRedisTemplate;

    @Value("${cache.ttl}")
    private long cacheTtl;

    public Flux<LinkResponse> getLinks(Long id) {
        log.debug("LinksReactiveCacheService: getLinks: id - {}", id);
        return reactiveRedisTemplate
            .opsForSet()
            .members(Long.toString(id))
            .map(data -> {
                log.debug("getLinks -> data - {}", data);
                return data;
            })
            .doOnError(error -> log.error("getLinks -> error - {}", error.getMessage()))
            .doOnTerminate(() -> log.debug("LinksReactiveCacheService: getLinks: finished id - {}", id));
    }

    @SneakyThrows
    public Mono<Boolean> setLinks(Long id, LinkResponse... links) {
        log.debug("LinksReactiveCacheService: setLink: id - {}", id);
        log.debug("LinksReactiveCacheService: setLink: link - {}", links);

        return reactiveRedisTemplate
            .opsForSet()
            .add(Long.toString(id), links)
            .then(reactiveRedisTemplate.expire(Long.toString(id), Duration.ofHours(cacheTtl)))
            .map(res -> {
                log.debug("expire end - {}", res);
                return res;
            })
            .doOnTerminate(() -> {
                log.debug("LinksReactiveCacheService: setLink: finished id - {}", id);
                log.debug("LinksReactiveCacheService: setLink: finished link - {}", links);
            })
            //            .publishOn(Schedulers.fromExecutor(executor))
            ;
    }

    public Mono<Long> removeChat(Long id) {
        log.debug("LinksReactiveCacheService: removeChat: id - {}", id);
        return reactiveRedisTemplate
            .delete(Long.toString(id))
            .doOnTerminate(() -> log.debug("LinksReactiveCacheService: removeChat: finished id - {}", id));
    }

    public Mono<Long> removeLink(Long id, LinkResponse link) {
        log.debug("LinksReactiveCacheService: removeLink: id - {}", id);
        log.debug("LinksReactiveCacheService: removeLink: link - {}", link);
        return reactiveRedisTemplate.opsForSet().remove(Long.toString(id), link).doOnTerminate(() -> {
            log.debug("LinksReactiveCacheService: removeLink: finished id - {}", id);
            log.debug("LinksReactiveCacheService: removeLink: finished link - {}", link);
        });
    }

    public Mono<Boolean> createKeyWithEmptyValue(Long id) {

        return setLinks(id, LinkResponse.of(-1L, null));
    }

    @PostConstruct
    public void checkRedisConnection() {
        reactiveRedisTemplate.getConnectionFactory().getReactiveConnection().ping().subscribe(res ->
            log.info(">>> Redis PING: " + res),
            e -> log.error("asd redis", e)
        );

        reactiveRedisTemplate.opsForSet().members("1020416851").subscribe(res -> {
            log.info(">>> Redis data: " + res);
        });
    }
}
