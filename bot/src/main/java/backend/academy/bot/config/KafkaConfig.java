package backend.academy.bot.config;

import backend.academy.dto.LinkUpdate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<Long, LinkUpdate> consumerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(
                props, new LongDeserializer(), new JsonDeserializer<>(LinkUpdate.class));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, LinkUpdate>>
            defaultConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        factory.setConcurrency(1);

        return factory;
    }

    @Bean
    public KafkaTemplate<Long, LinkUpdate> linkUpdateKafkaTemplate() {
        var props = kafkaProperties.buildProducerProperties(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "0");
        var factory = new DefaultKafkaProducerFactory<Long, LinkUpdate>(props);
        return new KafkaTemplate<>(factory);
    }
}
