package backend.academy.scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "retry")
public record RetryProperties(int maxAttempts, long waitDuration, long initialIntervalMillis, double exponential) {}
