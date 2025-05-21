package backend.academy.scrapper.testContainers;

import backend.academy.scrapper.controllers.LinkController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(BaseIntegrationTest.class)
@SpringBootTest(properties = {"spring.config.location=classpath:/application-test.yaml"})
public class TestApplication {
    @Autowired
    private LinkController linkController;

    @Test
    public void test() {
        Assertions.assertThrows(RuntimeException.class, () -> linkController.addLink(1L, null));
    }
}
