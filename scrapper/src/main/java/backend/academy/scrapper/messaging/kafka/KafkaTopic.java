package backend.academy.scrapper.messaging.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
    @Value("${kafka.topics.link-updates}")
    private String notificationsTopic;

    @Bean
    public NewTopic topicNotifications() {
        return TopicBuilder.name(notificationsTopic).build();
    }
}
