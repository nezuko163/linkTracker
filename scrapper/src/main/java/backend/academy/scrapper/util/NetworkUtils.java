package backend.academy.scrapper.util;

import backend.academy.exceptions.ServerException;
import backend.academy.scrapper.domain.model.ResponseWithHeadersForArray;
import backend.academy.scrapper.domain.model.ResponseWithHeadersForSingle;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public final class NetworkUtils {
    private static final Logger log = LogManager.getLogger(NetworkUtils.class);
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public <T> Mono<ResponseWithHeadersForSingle<T>> executeRequestAsync(
            WebClient.RequestHeadersSpec<?> req, Class<T> clazz) {
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
                .doOnNext(asd -> log.debug("body - {}", asd))
        //            .onErrorResume(error -> {
        //                if (error instanceof WebClientResponseException responseException) {
        //                    log.info("responseException - {}", responseException);
        //                    var response = responseException.getResponseBodyAs(ApiErrorResponse.class);
        //                    String message;
        //                    if (response != null) {
        //                        message = response.exceptionMessage();
        //                    } else {
        //                        message = "Ошибка";
        //                    }
        //                    return Mono.error(new RuntimeException(message));
        //                }
        //                return Mono.error(new RuntimeException("Ошибка выполнения запроса: " + error.getMessage()));
        //            })
        ;
    }

    public <T> Mono<ResponseWithHeadersForArray<T>> executeRequestAsyncWithHeadersForArray(
            WebClient.RequestHeadersSpec<?> req, Class<T> clazz) {
        return req.exchangeToMono(response -> {
                    if (response.statusCode().is5xxServerError()) {
                        throw new ServerException(response.statusCode().value(), "Ошибка сервера");
                    }
                    HttpStatusCode status = response.statusCode();
                    HttpHeaders headers = response.headers().asHttpHeaders();
                    return response.bodyToFlux(clazz)
                            .collectList()
                            .map(bodyList -> new ResponseWithHeadersForArray<>(bodyList, status, headers));
                })
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .doOnNext(asd -> log.debug("body - {}", asd))
                .onErrorResume(e -> {
                    log.error(e);
                    throw new RuntimeException(e.getMessage());
                });
    }
}
