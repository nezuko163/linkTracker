package backend.academy.scrapper.filters;

import backend.academy.StringConstants;
import backend.academy.exceptions.NotFoundError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public final class IpRateLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LogManager.getLogger(IpRateLimitInterceptor.class);
    private final ResilienceFilter resilienceFilter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String clientIp = extractIp(request);
        resilienceFilter.processRequestByIp(clientIp);

        return true; // Продолжаем выполнение запроса
    }

    private String extractIp(HttpServletRequest request) {
        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For")).orElse(request.getRemoteAddr());
        if (ip == null) {
            throw new NotFoundError(StringConstants.unknownIpAddress());
        }
        return ip;
    }
}
