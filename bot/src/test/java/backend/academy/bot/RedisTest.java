package backend.academy.bot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import backend.academy.bot.bot.BotHelper;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.config.KafkaConfig;
import backend.academy.bot.messaging.kafka.KafkaConsumerService;
import backend.academy.bot.services.ScrapperService;
import backend.academy.dto.LinkResponse;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@SpringBootTest
@Testcontainers
@EnableAutoConfiguration(
        exclude = {
            KafkaAutoConfiguration.class,
        })
@Import(TestcontainersConfiguration.class)
public class RedisTest {
    private static final Logger log = LogManager.getLogger(RedisTest.class);

    @Autowired
    private RedisTemplate<String, LinkResponse> redisTemplate;

    @MockitoBean
    private BotHelper botHelper;

    @Autowired
    private ScrapperService scrapperService;

    @MockitoBean
    private ScrapperClient scrapperClient;

    @MockitoBean
    private KafkaConfig kafkaConfig;

    @MockitoBean
    private KafkaConsumerService q;

    @Test
    @SneakyThrows
    public void whenCachedEmptyLinks() {
        long chatId = 1L;
        when(scrapperClient.getLinksAsync(chatId)).thenReturn(Mono.just(List.of()));

        log.info("cache - {}", redisTemplate.opsForSet().members(String.valueOf(chatId)));
        assertThat(redisTemplate.opsForSet().members(String.valueOf(chatId))).isEmpty();

        scrapperService.listLinks(chatId).subscribe();
        Thread.sleep(2000L);
        assertThat(redisTemplate.opsForSet().members(String.valueOf(chatId))).hasSize(1);
        log.info("cache - {}", redisTemplate.opsForSet().members(String.valueOf(chatId)));
        redisTemplate.delete(String.valueOf(chatId));
    }

    @Test
    @SneakyThrows
    public void whenCachedLinks() {
        long chatId = 1L;
        when(scrapperClient.getLinksAsync(chatId)).thenReturn(Mono.just(List.of(LinkResponse.of(1L, "qwe"))));

        log.info("cache - {}", redisTemplate.opsForSet().members(String.valueOf(chatId)));
        assertThat(redisTemplate.opsForSet().members(String.valueOf(chatId))).isEmpty();

        scrapperService.listLinks(chatId).subscribe();
        Thread.sleep(2000L);
        assertThat(Objects.requireNonNull(redisTemplate.opsForSet().members(String.valueOf(chatId))).stream()
                        .findFirst()
                        .get()
                        .url())
                .isEqualTo("qwe");
        redisTemplate.delete(String.valueOf(chatId));
    }
}
