package backend.academy.scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiter")
public record RateLimiterProperties(int limitForPeriod, int limitRefreshPeriod, long timeoutDuration) {}
