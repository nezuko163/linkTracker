package backend.academy.scrapper.testContainers;

import static backend.academy.scrapper.TestResources.CHAT_ID1;
import static backend.academy.scrapper.TestResources.CHAT_ID2;
import static backend.academy.scrapper.TestResources.FILTER1;
import static backend.academy.scrapper.TestResources.TAG1;
import static backend.academy.scrapper.TestResources.TAG2;
import static backend.academy.scrapper.TestResources.URL_SO1;
import static backend.academy.scrapper.TestResources.URL_SO2;
import static backend.academy.scrapper.TestResources.URL_SO3;
import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.repository.chats.chatStorage.ChatStorageRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.data.repository.tracking.filter.FilterRepository;
import backend.academy.scrapper.data.repository.tracking.linkRelations.RelationRepository;
import backend.academy.scrapper.data.repository.tracking.tag.TagRepository;
import backend.academy.scrapper.services.TrackingService;
import java.util.List;
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
public class LinkWithTgChatStorageRepositoryTest {
    private static final Logger log = LogManager.getLogger(LinkWithTgChatStorageRepositoryTest.class);

    @Autowired
    private TrackingService trackingService;

    @Autowired
    private ChatStorageRepository chatStorageRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private LinkStorageRepository linkRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FilterRepository filterRepository;

    @AfterEach
    public void tearDown() {
        relationRepository.__clear_forTest();
    }

    @Test
    void trackLinkByChat_1size_success() {
        trackingService.track(CHAT_ID1, URL_SO1, List.of(), List.of());

        var links = relationRepository.getTrackedLinksByChat(CHAT_ID1);

        assertThat(links).hasSize(1);
        assertThat(linkRepository.__findAll_forTest()).hasSize(1);
        links.forEach(chat -> {
            ;
            assertThat(chat.url()).isEqualTo(URL_SO1);
        });
    }

    @Test
    void trackLinkByChat_3size_success() {
        trackingService.track(CHAT_ID1, URL_SO1, List.of(), List.of());
        trackingService.track(CHAT_ID1, URL_SO2, List.of(), List.of());
        trackingService.track(CHAT_ID1, URL_SO3, List.of(), List.of());
        var set1 = relationRepository.getTrackedLinksByChat(CHAT_ID1);
        log.info("set1 - {}", set1);
        log.info("links - {}", linkRepository.__findAll_forTest());
        assertThat(set1).hasSize(3);
        assertThat(linkRepository.__findAll_forTest()).hasSize(3);
        set1.forEach(chat -> {
            assertThat(chat.url()).isIn(URL_SO1, URL_SO2, URL_SO3);
        });
    }

    @Test
    void trackLinkByChat_throw() {
        Assertions.assertThrows(NotFoundError.class, () -> trackingService.track(null, null, List.of(), List.of()));
        Assertions.assertThrows(NotFoundError.class, () -> trackingService.track(31L, null, List.of(), List.of()));
    }

    @Test
    void untrackLinkByChat_throw() {
        Assertions.assertThrows(NotFoundError.class, () -> trackingService.untrack(31L, URL_SO1));
    }

    @Test
    void untrackLinkByChat_success() {
        trackingService.track(CHAT_ID1, URL_SO1, List.of(), List.of());
        assertThat(linkRepository.__findAll_forTest()).hasSize(1);
        trackingService.untrack(CHAT_ID1, URL_SO1);
        assertThat(relationRepository.getTrackedLinksByChat(CHAT_ID1)).hasSize(0);
        assertThat(linkRepository.__findAll_forTest()).hasSize(0);
    }

    @Test
    void untrackLinkByChat_2chats_subscribe_and_1_remove_success() {
        trackingService.track(CHAT_ID1, URL_SO1, List.of(), List.of());
        trackingService.track(CHAT_ID2, URL_SO1, List.of(), List.of());
        assertThat(linkRepository.__findAll_forTest()).hasSize(1);
        assertThat(relationRepository.getTrackedLinksByChat(CHAT_ID1)).hasSize(1);
        trackingService.untrack(CHAT_ID1, URL_SO1);
        assertThat(linkRepository.__findAll_forTest()).hasSize(1);
        assertThat(relationRepository.getTrackedLinksByChat(CHAT_ID1)).hasSize(0);
    }

    @Test
    void trackingLinksByChat_2links_success() {
        trackingService.track(CHAT_ID1, URL_SO1, List.of(), List.of());
        trackingService.track(CHAT_ID1, URL_SO2, List.of(), List.of());

        var res = relationRepository.getTrackedLinksByChat(CHAT_ID1);
        assertThat(res).hasSize(2);
        res.forEach(log::info);
    }

    @Test
    void chatsThatTracingLink_throw() {
        Assertions.assertThrows(NotFoundError.class, () -> chatStorageRepository.getTgChatsTrackingLink(URL_SO1));
    }

    @Test
    void chatsThatTracingLink_1_chat() {
        trackingService.track(CHAT_ID1, URL_SO1, List.of(), List.of());
        assertThat(chatStorageRepository.getTgChatsTrackingLink(URL_SO1)).hasSize(1);
    }

    @Test
    void chatsThatTracingLink_2_chat() {
        trackingService.track(CHAT_ID1, URL_SO1, List.of(), List.of());
        trackingService.track(CHAT_ID2, URL_SO1, List.of(), List.of());
        assertThat(chatStorageRepository.getTgChatsTrackingLink(URL_SO1)).hasSize(2);
    }

    @Test
    void trackLinkByChat_with_tags_and_filters() {
        var link = trackingService.track(CHAT_ID1, URL_SO1, List.of(FILTER1), List.of(TAG1, TAG2));
        assertThat(link.filters()).hasSize(1);
        assertThat(link.tags()).hasSize(2);
        assertThat(tagRepository.getAll()).hasSize(2);
        assertThat(filterRepository.getAll()).hasSize(1);
    }
}
