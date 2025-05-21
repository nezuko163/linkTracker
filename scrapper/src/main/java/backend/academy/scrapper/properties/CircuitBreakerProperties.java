package backend.academy.scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "circuit-breaker")
public record CircuitBreakerProperties(
        int slidingWindowSize,
        int minimumNumberOfCalls,
        int failureRateThreshold,
        int waitDurationInOpenState,
        int permittedNumberOfCallsInHalfOpenState) {}
