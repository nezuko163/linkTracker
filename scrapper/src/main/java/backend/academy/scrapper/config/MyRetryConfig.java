package backend.academy.scrapper.config;

import backend.academy.exceptions.ServerException;
import backend.academy.scrapper.domain.model.ResponseWithHeaders;
import backend.academy.scrapper.properties.CircuitBreakerProperties;
import backend.academy.scrapper.properties.RateLimiterProperties;
import backend.academy.scrapper.properties.RetryProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MyRetryConfig {
    private static final Logger log = LogManager.getLogger(MyRetryConfig.class);
    private final RateLimiterProperties rateLimiterProperties;
    private final CircuitBreakerProperties circuitBreakerProperties;
    private final RetryProperties retryProperties;

    @Bean
    public Retry retryWebClient() {
        RetryConfig config = RetryConfig.<ResponseWithHeaders>custom()
                .maxAttempts(retryProperties.maxAttempts())
                .waitDuration(Duration.ofMillis(retryProperties.waitDuration()))
                .intervalFunction(IntervalFunction.ofExponentialBackoff(
                        retryProperties.initialIntervalMillis(), retryProperties.exponential()))
                .retryOnResult(response -> {
                    log.debug("code - {}", response.status().value());
                    return (response.status().is5xxServerError()
                            || response.status().value() == 429
                            || response.status().value() == 408);
                })
                .retryOnException(exception -> {
                    if (exception instanceof ServerException serverException) {
                        log.error("ОШИБКА СЕРВЕРА - {}", serverException.getMessage());
                        return true;
                    }
                    return false;
                })
                .build();

        return Retry.of("retryWebClient", config);
    }

    @Bean
    public CircuitBreaker circuitBreakerWebClient() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(circuitBreakerProperties.slidingWindowSize())
                .minimumNumberOfCalls(circuitBreakerProperties.minimumNumberOfCalls())
                .failureRateThreshold(circuitBreakerProperties.failureRateThreshold())
                .waitDurationInOpenState(Duration.ofSeconds(circuitBreakerProperties.waitDurationInOpenState()))
                .permittedNumberOfCallsInHalfOpenState(circuitBreakerProperties.permittedNumberOfCallsInHalfOpenState())
                .build();

        return CircuitBreaker.of("circuitBreakerWebClient", config);
    }

    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(rateLimiterProperties.limitForPeriod())
                .limitRefreshPeriod(Duration.ofSeconds(rateLimiterProperties.limitRefreshPeriod()))
                .timeoutDuration(Duration.ofMillis(rateLimiterProperties.timeoutDuration()))
                .build();
    }
}
