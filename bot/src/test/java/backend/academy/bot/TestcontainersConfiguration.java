package backend.academy.bot;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

// isolated from the "scrapper" module's containers!
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @RestartScope
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7.4.2")).withExposedPorts(6379);
    }
    //
    //    @Bean
    //    @RestartScope
    //    @ServiceConnection
    //    ConfluentKafkaContainer kafkaContainer() {
    //        return new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.0").withExposedPorts(9092);
    //    }
}
