package backend.academy.bot;

import backend.academy.bot.config.BotConfig;
import backend.academy.bot.properties.CircuitBreakerProperties;
import backend.academy.bot.properties.RetryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BotConfig.class, CircuitBreakerProperties.class, RetryProperties.class})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
