package backend.academy.bot;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.bot.services.ScrapperService;
import backend.academy.dto.LinkResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

@SpringBootTest
public class LinksListFormatTest {
    @Autowired
    private ScrapperService repository;

    @MockitoBean
    ScrapperService scrapperRepository;

    @Test
    public void test() {
        Mockito.when(scrapperRepository.listLinks(2L))
                .thenReturn(Mono.just(
                        List.of(new LinkResponse(1L, "asd", null, null), new LinkResponse(1L, "qwe", null, null))));
        assertThat(repository.listLinks(2L).block()).isEqualTo("1. asd\n2. qwe\n");
    }

    @TestConfiguration
    static class Config {}
}
