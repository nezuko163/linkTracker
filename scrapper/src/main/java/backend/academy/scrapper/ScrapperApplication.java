package backend.academy.scrapper;

import backend.academy.scrapper.properties.CircuitBreakerProperties;
import backend.academy.scrapper.properties.ExternalUriProperties;
import backend.academy.scrapper.properties.RateLimiterProperties;
import backend.academy.scrapper.properties.RetryProperties;
import backend.academy.scrapper.properties.ScrapperProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({
    ExternalUriProperties.class,
    RetryProperties.class,
    ScrapperProperties.class,
    CircuitBreakerProperties.class,
    RateLimiterProperties.class
})
public class ScrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
