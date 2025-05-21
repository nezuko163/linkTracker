package backend.academy.scrapper.unit;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.domain.model.serviceLink.GithubLink;
import backend.academy.scrapper.domain.model.serviceLink.StackOverflowLink;
import backend.academy.scrapper.util.LinkParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinkParserTest {

    @Test
    public void testParser_GithubLink() {
        assertThat(LinkParser.parseUrl("https://github.com/nezuko163/TestTask1"))
                .isEqualTo(new GithubLink("https://github.com/nezuko163/TestTask1", "nezuko163", "TestTask1"));

        assertThat(
                        LinkParser.parseUrl(
                                "https://github.com/central-university-dev/backend-academy-2025-spring/blob/main/Java/seminar3/src/test/java/ru/tbank/sem3/unit/CheckEvenControllerTest.java"))
                .isEqualTo(new GithubLink(
                        "https://github.com/central-university-dev/backend-academy-2025-spring/blob/main/Java/seminar3/src/test/java/ru/tbank/sem3/unit/CheckEvenControllerTest.java",
                        "central-university-dev",
                        "backend-academy-2025-spring"));
    }

    @Test
    public void testParser_SOLink() {
        assertThat(
                        LinkParser.parseUrl(
                                "https://stackoverflow.com/questions/78785105/stateflow-value-changes-but-subscribers-are-not-notified"))
                .isEqualTo(new StackOverflowLink(
                        "https://stackoverflow.com/questions/78785105/stateflow-value-changes-but-subscribers-are-not-notified",
                        78785105L));
    }

    @Test
    public void testParser_StackOverflowLink() {
        var a = "https://stackoverflow.com/questions/212358/binary-search-bisection-in-python";
        LinkParser.parseUrl(a);
    }

    @Test
    public void testParser_Null() {
        Assertions.assertThrows(NotFoundError.class, () -> LinkParser.parseUrl("https://vk.com/nezuko163/TestTask2"));
    }
}
