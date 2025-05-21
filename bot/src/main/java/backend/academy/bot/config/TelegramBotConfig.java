package backend.academy.bot.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {
    private static final Logger log = LogManager.getLogger(TelegramBotConfig.class);
    private final BotConfig botConfig;

    @Bean
    public TelegramBot telegramBot() {
        log.info("Initializing TelegramBot, token - {}", botConfig.telegramToken());
        return new TelegramBot(botConfig.telegramToken());
    }
}
