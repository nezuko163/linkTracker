package backend.academy.scrapper.testContainers;

import static backend.academy.scrapper.TestResources.LINK_SO1;
import static backend.academy.scrapper.TestResources.URL_SO1;
import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.data.repository.links.subscription.LinkSubscriptionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(BaseIntegrationTest.class)
@SpringBootTest(properties = {"spring.config.location=classpath:/application-test.yaml"})
public class LinkRepositoryTest {

    private static final Logger log = LogManager.getLogger(LinkRepositoryTest.class);

    @Autowired
    private LinkStorageRepository linkRepository;

    @Autowired
    private LinkSubscriptionRepository subscriptionRepository;

    @AfterEach
    public void tearDown() {
        linkRepository.clear();
    }

    @Test
    public void addSubscriberToLink_equals() {
        linkRepository.saveLink(URL_SO1);
        subscriptionRepository.addSubscriber(URL_SO1);
        assertThat(linkRepository.findAllPaging(0).data().getFirst()).isEqualTo(LINK_SO1);
        assertThat(linkRepository.findAllPaging(0).data().getFirst().lastCheckedDate())
                .isNotNull();
    }

    @Test
    public void addSubscriberToLink_() {
        linkRepository.saveLink(URL_SO1);
        var link1 = subscriptionRepository.addSubscriber(URL_SO1);
        var link2 = subscriptionRepository.addSubscriber(URL_SO1);

        log.info("link1 - {}", link1);
        log.info("link2 - {}", link2);
        log.info("links - {}", linkRepository.__findAll_forTest());
        assertThat(linkRepository.findAllPaging(0).data().size()).isEqualTo(1);
        assertThat(linkRepository.__findAll_forTest().stream()
                        .toList()
                        .getFirst()
                        .subscribers())
                .isEqualTo(2);
        assertThat(linkRepository.findAllPaging(0).data().getFirst()).isEqualTo(LINK_SO1);
    }

    @Test
    public void addSubscriberToLink_null() {
        Assertions.assertThrows(NotFoundError.class, () -> subscriptionRepository.addSubscriber(null));
    }

    @Test
    public void removeSubscriberFromLink_success() {
        linkRepository.saveLink(URL_SO1);
        subscriptionRepository.addSubscriber(URL_SO1);
        assertThat(linkRepository.findAllPaging(0).data().size()).isEqualTo(1);
        subscriptionRepository.removeSubscriber(URL_SO1);
        assertThat(linkRepository.findAllPaging(0).data().size()).isEqualTo(0);
    }

    @Test
    public void removeSubscriberFromLink_throw() {
        Assertions.assertThrows(NotFoundError.class, () -> subscriptionRepository.removeSubscriber(URL_SO1));
    }

    @Test
    public void getLink_success() {
        linkRepository.saveLink(URL_SO1);
        var link = linkRepository.findLinkByUrl(URL_SO1);
        log.info("link - {}", link);
        assertThat(link.url()).isEqualTo(URL_SO1);
    }

    @Test
    public void getLink_null() {
        Assertions.assertThrows(NotFoundError.class, () -> linkRepository.findLinkByUrl(URL_SO1));
    }
}
