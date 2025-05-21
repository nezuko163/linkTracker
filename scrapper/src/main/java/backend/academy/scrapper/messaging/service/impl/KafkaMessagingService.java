package backend.academy.scrapper.messaging.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.messaging.service.FailureMessageSupport;
import backend.academy.scrapper.messaging.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("kafka")
@RequiredArgsConstructor
public class KafkaMessagingService implements MessagingService, FailureMessageSupport {
    private static final Logger log = LogManager.getLogger(KafkaMessagingService.class);

    @Value("${kafka.topics.link-updates}")
    private String notificationsTopic;

    private final ApplicationContext applicationContext;
    private final KafkaTemplate<Long, LinkUpdate> kafkaTemplate;

    @Override
    public void sendUpdate(LinkUpdate linkUpdate, boolean firstAttempt) {
        log.debug("Sending update to topic {}", notificationsTopic);
        kafkaTemplate.send(notificationsTopic, linkUpdate).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error sending message to topic {}", notificationsTopic, ex);
                if (ex instanceof KafkaProducerException kafkaException) {
                    kafkaException.getFailedProducerRecord();
                }
            }
        });
    }

    @Override
    public void onFailureSendMessage(LinkUpdate linkUpdate) {
        try {
            MessagingService fallback = applicationContext.getBean(HttpMessagingService.class);
            fallback.sendUpdate(linkUpdate, false);
        } catch (BeansException e) {
            log.error("Failed to send update", e);
        }
    }
}
