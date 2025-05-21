package backend.academy.scrapper.wiremock;

import backend.academy.scrapper.controllers.HelpController;
import backend.academy.scrapper.data.repository.chats.tgChatStorage.TgChatStorageRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.properties.CircuitBreakerProperties;
import backend.academy.scrapper.properties.ExternalUriProperties;
import backend.academy.scrapper.properties.RateLimiterProperties;
import backend.academy.scrapper.properties.RetryProperties;
import backend.academy.scrapper.properties.ScrapperProperties;
import backend.academy.scrapper.services.ChatService;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.config.location=classpath:/application-test.yaml",
        classes = {WireMockTestConfiGuration.class, HelpController.class})
@EnableAutoConfiguration(
        exclude = {
            DataSourceAutoConfiguration.class,
            KafkaAutoConfiguration.class,
            RedisAutoConfiguration.class,
        })
@EnableConfigurationProperties(
        value = {
            CircuitBreakerProperties.class,
            ExternalUriProperties.class,
            RateLimiterProperties.class,
            RetryProperties.class,
            ScrapperProperties.class
        })
public class RateLimiterTest {
    private static final Logger log = LogManager.getLogger(RateLimiterTest.class);

    @Autowired
    private ApplicationContext context;

    @LocalServerPort
    private int port;

    @MockitoBean
    ChatService chatService;

    @MockitoBean
    LinkStorageRepository linkStorageRepository;

    @MockitoBean
    TgChatStorageRepository tgChatStorageRepository;

    WebClient webClient = WebClient.builder().build();

    @Test
    @SneakyThrows
    public void testRateLimiter() {

        for (int i = 0; i < 6; i++) {
            webClient
                    .get()
                    .uri("http://localhost:" + port + "/api/v1/all")
                    .exchangeToMono(data -> data.bodyToMono(String.class))
                    .subscribe(System.out::println, System.out::println);
        }

        Thread.sleep(3000l);
    }
}
