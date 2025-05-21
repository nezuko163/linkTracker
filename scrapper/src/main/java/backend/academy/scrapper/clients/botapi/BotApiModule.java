package backend.academy.scrapper.clients.botapi;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.util.NetworkUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BotApiModule {
    private final WebClient webClient;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;
    private final NetworkUtils networkUtils;

    private final Logger log = LoggerFactory.getLogger(BotApiModule.class);

    public BotApiModule(
            @Qualifier("botWebClient") WebClient webClient,
            Retry retry,
            CircuitBreaker circuitBreaker,
            NetworkUtils networkUtils) {
        this.webClient = webClient;
        this.retry = retry;
        this.circuitBreaker = circuitBreaker;
        this.networkUtils = networkUtils;
    }

    public Mono<Boolean> sendUpdates(LinkUpdate linkUpdate) {
        log.info("Отправление обновлений: {}", linkUpdate);

        return webClient
                .post()
                .uri("updates")
                .bodyValue(linkUpdate)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return Mono.empty();
                    }
                })
                .onErrorResume(error -> {
                    log.error(error.getMessage());
                    return Mono.empty();
                });
    }
}
