package backend.academy.scrapper.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

import backend.academy.scrapper.clients.github.async.GithubApiAsync;
import backend.academy.scrapper.util.NetworkUtils;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

@WebServiceClientTest(properties = "spring.config.location=classpath:/application-test.yaml")
@AutoConfigureWireMock(port = 8089)
// @EnableAutoConfiguration(exclude = {
//    DataSourceAutoConfiguration.class,
//    KafkaAutoConfiguration.class,
//    RedisAutoConfiguration.class,
// })
@ComponentScan(
        basePackages = {
            "backend.academy.scrapper.config", // WebClientConfig
            "backend.academy.scrapper.properties", // ScrapperProperties
            "backend.academy.scrapper.util", // NetworkUtils и др. утилиты
            "backend.academy.scrapper.filters"
        })
@Import(GithubWireMockTest.TestConfig.class)
@ExtendWith(SpringExtension.class)
public class GithubWireMockTest {
    private static final Logger log = LogManager.getLogger(GithubWireMockTest.class);

    @Autowired
    GithubApiAsync githubApiAsync;

    static String url = "/repos/owner/repo/pulls";

    @Test
    @SneakyThrows
    public void retryCompleteCauseManyFailures() {
        stubFor(
                get(urlPathEqualTo(url))
                        .willReturn(
                                aResponse()
                                        .withStatus(429)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                        [
                          { "message": "too many request" },
                          { "code": 429 }
                        ]
                        """)));
        githubApiAsync.getPullRequestsOfRepo("owner", "repo").subscribe(res -> log.info("asd - {}", res));
        Thread.sleep(2000);
        StepVerifier.create(githubApiAsync.getPullRequestsOfRepo("owner", "repo"))
                .expectComplete()
                .verify();
    }

    @Test
    @SneakyThrows
    void retrySuccess() {
        stubFor(
                get(urlPathEqualTo(url))
                        .inScenario("RetryScenario")
                        .whenScenarioStateIs(STARTED)
                        .willSetStateTo("SecondAttempt")
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                        [
                          { "message": "too many request" },
                          { "code": 429 }
                        ]
                        """)));

        stubFor(
                get(urlPathEqualTo(url))
                        .inScenario("RetryScenario")
                        .whenScenarioStateIs("SecondAttempt")
                        .willSetStateTo("ThirdAttempt")
                        .willReturn(
                                aResponse()
                                        .withStatus(429)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                        [
                          { "message": "too many request" },
                          { "code": 429 }
                        ]
                        """)));

        stubFor(
                get(urlPathEqualTo(url))
                        .inScenario("RetryScenario")
                        .whenScenarioStateIs("ThirdAttempt")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                        [
                            {
                              "number": 42,
                              "body": "This is the description of the pull request.",
                              "title": "Add new feature",
                              "created_at": "2025-05-15T10:30:00Z",
                              "html_url": "https://github.com/example/repo/pull/42",
                              "user": {
                                "login": "octocat"
                              }
                            }
                        ]
                        """)));
        StepVerifier.create(githubApiAsync.getPullRequestsOfRepo("owner", "repo"))
                .then(() -> log.info("AUEAUE"))
                .expectComplete()
                .verify();
    }

    @Ignore
    @SneakyThrows
    @Test
    public void circuitBreakerTest() {
        for (int i = 0; i < 5; i++) {
            stubFor(
                    get(urlPathEqualTo(url))
                            .inScenario("CB test")
                            .willReturn(
                                    aResponse()
                                            .withStatus(500)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody(
                                                    """
                            [
                              { "message": "server error" },
                              { "code": 500 }
                            ]
                            """)));
        }

        for (int i = 0; i < 5; i++) {
            get(urlPathEqualTo(url))
                    .inScenario("CB test")
                    .willReturn(
                            aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(
                                            """
                        [
                            {
                              "number": 42,
                              "body": "This is the description of the pull request.",
                              "title": "Add new feature",
                              "created_at": "2025-05-15T10:30:00Z",
                              "html_url": "https://github.com/example/repo/pull/42",
                              "user": {
                                "login": "octocat"
                              }
                            }
                        ]
                        """));
        }

        for (int i = 0; i < 10; i++) {
            githubApiAsync.getPullRequestsOfRepo("owner", "repo").subscribe(res -> log.info("asd - {}", res));
        }

        Thread.sleep(3000l);

        verify(10, getRequestedFor(urlPathEqualTo(url)));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public GithubApiAsync githubApiAsync(@Qualifier("ghWebClient") WebClient webClient, NetworkUtils networkUtils) {
            return new GithubApiAsync(webClient, networkUtils);
        }
    }
}
