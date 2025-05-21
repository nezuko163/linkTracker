package backend.academy.bot.client;

import backend.academy.Result;
import backend.academy.bot.domain.model.ResponseWithHeadersForSingle;
import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import backend.academy.exceptions.ServerException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// TODO: Вынести кэширование от клиента
@Component
@RequiredArgsConstructor
public class ScrapperClient {
    private static final Logger log = LogManager.getLogger(ScrapperClient.class);
    private final WebClient webClient;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public Mono<Result<LinkResponse>> addLinkAsync(Long chatId, AddLinkRequest link) {
        log.debug("start addLinkAsync");
        return executeRequestAsync(
                        webClient
                                .post()
                                .uri("links")
                                .header("Tg-Chat-Id", chatId.toString())
                                .bodyValue(link),
                        LinkResponse.class)
                .map(res -> {
                    log.debug("end addLinkAsync - {}", res);
                    return res;
                });
    }

    public Mono<List<LinkResponse>> getLinksAsync(Long chatId) {
        return executeRequestAsync(
                        webClient.get().uri("links").header("Tg-Chat-Id", chatId.toString()), ListLinksResponse.class)
                .map(res -> {
                    if (res.status() != Result.Status.SUCCESS) {
                        throw new RuntimeException("getLinksAsync - " + res.message());
                    }
                    return res.data().links();
                });
    }

    public Mono<Result<LinkResponse>> deleteLinkAsync(Long chatId, RemoveLinkRequest link) {
        return executeRequestAsync(
                webClient
                        .method(HttpMethod.DELETE)
                        .uri("links")
                        .header("Tg-Chat-Id", chatId.toString())
                        .bodyValue(link),
                LinkResponse.class);
    }

    public Mono<Result<LinkResponse>> registerChatAsync(Long chatId) {
        return executeRequestAsync(webClient.post().uri(String.format("tg-chat/%d", chatId)), LinkResponse.class);
    }

    protected <T> Mono<Result<T>> executeRequestAsync(WebClient.RequestHeadersSpec<?> req, Class<T> clazz) {
        log.debug("start executeRequestAsync");
        return req.exchangeToMono(response -> {
                    if (response.statusCode().is5xxServerError()) {
                        throw new ServerException(response.statusCode().value(), "Ошибка сервера");
                    }
                    HttpStatusCode status = response.statusCode();
                    HttpHeaders headers = response.headers().asHttpHeaders();
                    return response.bodyToMono(clazz)
                            .map(body -> new ResponseWithHeadersForSingle<T>(body, status, headers));
                })
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(res -> {
                    log.debug("end executeRequestAsync asd asd asd asd a - {}", res);
                    return Result.success(res.body());
                })
                .doOnTerminate(() -> log.debug("end executeRequestAsync"))
                .onErrorResume(error -> Mono.just(Result.failure("Ошибка выполнения запроса: " + error.getMessage())));
    }
}
