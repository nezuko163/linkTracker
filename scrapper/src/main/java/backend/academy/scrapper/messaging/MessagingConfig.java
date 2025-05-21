package backend.academy.scrapper.messaging;

import backend.academy.scrapper.messaging.service.MessagingService;
import backend.academy.scrapper.messaging.service.impl.HttpMessagingService;
import backend.academy.scrapper.messaging.service.impl.KafkaMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MessagingConfig {
    private final HttpMessagingService httpMessagingService;
    private final KafkaMessagingService kafkaMessagingService;

    @Value("${app.messaging}")
    private String messagingType;

    @Bean
    public MessagingService messagingService() {
        return switch (messagingType) {
            case "http" -> httpMessagingService;
            case "kafka" -> kafkaMessagingService;
            default -> throw new IllegalArgumentException("Unknown messaging type: " + messagingType);
        };
    }
}
