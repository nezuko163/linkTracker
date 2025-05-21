package backend.academy.scrapper.testContainers.v2;

import static backend.academy.scrapper.TestResources.URL_SO1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.data.repository.links.subscription.LinkSubscriptionRepository;
import backend.academy.scrapper.testContainers.BaseIntegrationTest;
import backend.academy.scrapper.testContainers.LinkRepositoryTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(BaseIntegrationTest.class)
@SpringBootTest(properties = {"spring.config.location=classpath:/application-test.yaml"})
public class LinkSubscriptionRepositoryTest {
    private static final Logger log = LogManager.getLogger(LinkRepositoryTest.class);

    @Autowired
    private LinkStorageRepository linkRepository;

    @Autowired
    private LinkSubscriptionRepository linkSubscriptionRepository;

    @Test
    public void addSubscriberTest_success() {
        var link = linkRepository.saveLink(URL_SO1);
        linkSubscriptionRepository.addSubscriber(link.id(), link.url());
        assertThat(linkRepository.findAllPaging(0).data().getFirst().subscribers())
                .isEqualTo(1);
        linkSubscriptionRepository.addSubscriber(link.id(), link.url());
        assertThat(linkRepository.findAllPaging(0).data().getFirst().subscribers())
                .isEqualTo(2);
    }

    @Test
    public void addSubscriber_throws() {
        assertThrows(NotFoundError.class, () -> linkSubscriptionRepository.addSubscriber(URL_SO1));
    }

    @Test
    public void removeSubscriberTest_success() {
        var link = linkRepository.saveLink(URL_SO1);
        linkSubscriptionRepository.addSubscriber(link.id(), link.url());
        assertThat(linkRepository.findAllPaging(0).data().getFirst().subscribers())
                .isEqualTo(1);
        linkSubscriptionRepository.removeSubscriber(link.id());
        assertThat(linkRepository.findAllPaging(0).data()).hasSize(0);
    }

    @Test
    public void removeSubscriberTest_throws() {
        assertThrows(NotFoundError.class, () -> linkSubscriptionRepository.removeSubscriber(URL_SO1));
    }

    @AfterEach
    public void tearDown() {
        linkRepository.clear();
    }
}
