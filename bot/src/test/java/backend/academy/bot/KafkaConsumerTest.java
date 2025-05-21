package backend.academy.bot;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import backend.academy.bot.services.LinkUpdateService;
import backend.academy.dto.LinkUpdate;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
public class KafkaConsumerTest {

    private static final Logger log = LogManager.getLogger(KafkaConsumerTest.class);

    @Value("${kafka.topics.link-updates}")
    private String notificationsTopic;

    @Container
    protected static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.0");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("kafka.retry.attempts", () -> "2");
        registry.add("kafka.retry.delay", () -> "100");
        registry.add("kafka.retry.multiplier", () -> "1.0");
    }

    @MockitoBean
    private LinkUpdateService linkUpdateService;

    @Autowired
    private KafkaTemplate<String, LinkUpdate> kafkaTemplate;

    @Test
    public void testValidMessageListen_success() {
        LinkUpdate update = new LinkUpdate(1L, "https://example.com", "description", List.of(42L));

        kafkaTemplate.send(notificationsTopic, update);
        kafkaTemplate.flush();

        await().atMost(10, SECONDS)
                .untilAsserted(() -> verify(linkUpdateService).update(update));
    }

    @Test
    public void testInvalidMessageListen_success() {
        LinkUpdate update = new LinkUpdate(1L, "фв", "", List.of());

        kafkaTemplate.send(notificationsTopic, update);
        kafkaTemplate.flush();
        //        var dltRecord = KafkaTestUtils.getOneRecord(kafka.getBootstrapServers(), "default", notificationsTopic
        // + "-dlt", 0, false, false, Duration.ofSeconds(10));
        //        assertThat(dltRecord).isNotNull();
    }

    @TestConfiguration
    public static class KafkaTestConfig {

        @Bean
        public ProducerFactory<String, LinkUpdate> producerFactory(
                @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
            Map<String, Object> config = Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        public KafkaTemplate<String, LinkUpdate> kafkaTemplate(ProducerFactory<String, LinkUpdate> pf) {
            return new KafkaTemplate<>(pf);
        }
    }
}
