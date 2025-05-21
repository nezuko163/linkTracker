package backend.academy.scrapper.testContainers;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.data.repository.chats.tgChatStorage.TgChatStorageRepository;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(BaseIntegrationTest.class)
@SpringBootTest(properties = {"spring.config.location=classpath:/application-test.yaml"})
public class TgChatStorageRepositoryTest {

    @Autowired
    private TgChatStorageRepository tgChatStorageRepository;

    @AfterEach
    void tearDown() {
        tgChatStorageRepository.clear();
    }

    @Test
    void registerChat_test() {
        tgChatStorageRepository.registerChat(1L);
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(1);
        tgChatStorageRepository.registerChat(1L);
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(1);
        tgChatStorageRepository.registerChat(2L);
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(2);
    }

    @Test
    void removeChat_success() {
        tgChatStorageRepository.registerChat(1L);
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(1);
        tgChatStorageRepository.removeChat(1L);
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(0);
    }

    @Test
    void removeChat_false() {
        assertThat(tgChatStorageRepository.removeChat(1L)).isFalse();
    }

    @Test
    void isChatRegistered_true() {
        tgChatStorageRepository.registerChat(1L);
        assertThat(tgChatStorageRepository.isChatRegistered(1L)).isTrue();
    }

    @Test
    void isChatRegistered_false() {
        tgChatStorageRepository.registerChat(1L);
        assertThat(tgChatStorageRepository.isChatRegistered(2L)).isFalse();
    }

    @Test
    void registeredChats_test() {
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(0);
        tgChatStorageRepository.registerChat(1L);
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(1);
        tgChatStorageRepository.registerChat(2L);
        assertThat(tgChatStorageRepository.registeredChats()).hasSize(2);
        assertThat(tgChatStorageRepository.registeredChats()).isEqualTo(Set.of(1L, 2L));
    }
}
