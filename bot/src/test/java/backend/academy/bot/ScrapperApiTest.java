package backend.academy.bot;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.RemoveLinkRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScrapperApiTest {
    private static final Logger log = LogManager.getLogger(ScrapperApiTest.class);

    @Autowired
    private ScrapperClient scrapperApi;

    //    @Test
    void addLink_ApiError() {
        var a = scrapperApi.addLinkAsync(1L, new AddLinkRequest("", null, null));

        log.info(a);
        assertThat(a).isInstanceOf(ApiErrorResponse.class);
    }

    //    @Test
    void getLinks_ApiError() {
        assertThat(scrapperApi.getLinksAsync(-2L)).isInstanceOf(ApiErrorResponse.class);
    }

    //    @Test
    void deleteLink_ApiError() {
        assertThat(scrapperApi.deleteLinkAsync(1L, new RemoveLinkRequest(null))).isInstanceOf(ApiErrorResponse.class);
    }

    //    @Test
    void registerChat_apiError() {
        assertThat(scrapperApi.registerChatAsync(null)).isInstanceOf(ApiErrorResponse.class);
    }
}
