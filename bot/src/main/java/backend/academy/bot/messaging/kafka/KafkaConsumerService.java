package backend.academy.bot.messaging.kafka;

import static org.springframework.kafka.retrytopic.TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE;

import backend.academy.bot.services.LinkUpdateService;
import backend.academy.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private static final Logger log = LogManager.getLogger(KafkaConsumerService.class);
    private final LinkUpdateService linkUpdateService;

    @KafkaListener(
            topics = "${kafka.topics.link-updates}",
            groupId = "default",
            containerFactory = "defaultConsumerFactory")
    @RetryableTopic(
            backoff =
                    @Backoff(
                            delayExpression = "${kafka.retry.delay:3000}",
                            multiplierExpression = "${kafka.retry.multiplier:2.0}"),
            attempts = "${kafka.retry.attempts:4}",
            kafkaTemplate = "linkUpdateKafkaTemplate",
            topicSuffixingStrategy = SUFFIX_WITH_INDEX_VALUE,
            include = RuntimeException.class)
    public void consume(ConsumerRecord<String, LinkUpdate> record, Acknowledgment acknowledgment) {
        log.info(
                """
                Получено сообщение:
                value - {}
                partition - {}
                offset - {}
                key - {}
                time - {}
                """,
                record.value(),
                record.partition(),
                record.offset(),
                record.key(),
                record.timestamp());
        try {
            if (record.value() == null
                    || record.value().tgChatIds() == null
                    || record.value().tgChatIds().isEmpty()
                    || record.value().description() == null
                    || record.value().description().isEmpty()) {
                log.error("value - {}", record.value().toString());
                throw new RuntimeException("Получено невалидное сообщение");
            }
            linkUpdateService.update(record.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка", e);
            throw e;
        }
    }
}
