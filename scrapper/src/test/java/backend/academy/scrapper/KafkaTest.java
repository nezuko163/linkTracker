package backend.academy.scrapper;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.messaging.service.impl.KafkaMessagingService;
import backend.academy.scrapper.testContainers.BaseIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@Import(BaseIntegrationTest.class)
@Testcontainers
@SpringBootTest(properties = {"spring.config.location=classpath:/application-test.yaml"})
public class KafkaTest {

    private static final Logger log = LogManager.getLogger(KafkaTest.class);

    @Value("${kafka.topics.link-updates}")
    private String notificationsTopic;

    @Autowired
    private KafkaMessagingService service;

    @Container
    protected static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.0");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    @SneakyThrows
    void sendUpdate_success() {
        service.sendUpdate(new LinkUpdate(12L, "asd", "asd", List.of(1L, 2L)));

        String kafkaBootstrapServers = kafka.getBootstrapServers();

        var a = KafkaTestUtils.getOneRecord(
                kafkaBootstrapServers, "test-group", notificationsTopic, 0, false, false, Duration.ofSeconds(10));

        assertThat(a).isNotNull();
        log.info("key - {}", a.key());
        log.info("value - {}", a.value());
        log.info("topic - {}", a.topic());
        log.info("values cladd - {}", a.value().getClass());
        assertThat(a.key()).isNull();
        assertThat(a.value()).isNotNull();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = (String) a.value();
        LinkUpdate map = objectMapper.readValue(json, LinkUpdate.class);

        assertThat(map.url()).isEqualTo("asd");
        assertThat(map.description()).isEqualTo("asd");
        assertThat(map.tgChatIds()).isEqualTo(List.of(1L, 2L));
        assertThat(map.id()).isEqualTo(12L);
    }
}
