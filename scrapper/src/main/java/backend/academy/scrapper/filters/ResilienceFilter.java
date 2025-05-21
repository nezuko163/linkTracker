package backend.academy.scrapper.filters;

import backend.academy.StringConstants;
import backend.academy.exceptions.NotFoundError;
import backend.academy.exceptions.TooManyRequestsError;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class ResilienceFilter {
    private final RateLimiterConfig rateLimiterConfig;
    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    public void processRequestByIp(String clientIp) {
        if (clientIp == null) {
            throw new NotFoundError(StringConstants.unknownIpAddress());
        }
        if (!rateLimiterMap.computeIfAbsent(clientIp, this::createRateLimiter).acquirePermission()) {
            throw new TooManyRequestsError(clientIp);
        }
    }

    private RateLimiter createRateLimiter(String ip) {
        return RateLimiter.of(ip, rateLimiterConfig);
    }
}
