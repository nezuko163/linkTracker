package backend.academy.scrapper.testContainers.v2;

import static backend.academy.scrapper.TestResources.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.domain.model.ObservableService;
import backend.academy.scrapper.testContainers.BaseIntegrationTest;
import backend.academy.scrapper.testContainers.LinkRepositoryTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "spring.config.location=classpath:/application-test.yaml")
@ImportAutoConfiguration(
        exclude = {
            KafkaAutoConfiguration.class,
            RedisAutoConfiguration.class,
            RedisRepositoriesAutoConfiguration.class,
        })
@Import(BaseIntegrationTest.class)
public class LinkStorageRepositoryTest {
    private static final Logger log = LogManager.getLogger(LinkRepositoryTest.class);

    @Autowired
    private LinkStorageRepository linkRepository;

    @AfterEach
    public void tearDown() {
        linkRepository.clear();
    }

    @Test
    public void saveLink_save1Link_success() {
        linkRepository.saveLink(URL_SO1);
        assertThat(linkRepository.findAllPaging(0).data().getFirst().url()).isEqualTo(URL_SO1);
        assertThat(linkRepository.findAllPaging(0).data().getFirst().service())
                .isEqualTo(ObservableService.STACK_OVERFLOW);
    }

    @Test
    public void saveLink_save2Links_success() {
        linkRepository.saveLink(URL_SO1);
        assertThat(linkRepository.findAllPaging(0).data().getFirst().url()).isEqualTo(URL_SO1);
        assertThat(linkRepository.findAllPaging(0).data().getFirst().service())
                .isEqualTo(ObservableService.STACK_OVERFLOW);
        linkRepository.saveLink(URL_SO2);
        assertThat(linkRepository.findAllPaging(0).data()).hasSize(2);
    }

    @Test
    public void saveLink_throws() {
        assertThrows(NotFoundError.class, () -> linkRepository.saveLink("asd"));
    }
}
