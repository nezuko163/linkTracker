package backend.academy.scrapper.config;

import backend.academy.scrapper.filters.ResilienceFilter;
import backend.academy.scrapper.properties.ExternalUriProperties;
import backend.academy.scrapper.properties.ScrapperProperties;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    private static final Logger log = LogManager.getLogger(WebClientConfig.class);
    private final String GH_API_KEY;
    private final ResilienceFilter resilienceFilter;
    private final ExternalUriProperties externalUriProperties;

    @Value("${app.timeout}")
    private long timeout;

    public WebClientConfig(
            ScrapperProperties scrapperProperties,
            ResilienceFilter resilienceFilter,
            ExternalUriProperties externalUriProperties) {
        GH_API_KEY = scrapperProperties.githubToken();
        this.resilienceFilter = resilienceFilter;
        this.externalUriProperties = externalUriProperties;
    }

    @Bean("soWebClient")
    public WebClient soWebClient() {
        return webClientBuilder().build();
    }

    @Bean("ghWebClient")
    public WebClient ghWebClient() {
        return webClientBuilder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + GH_API_KEY)
                .baseUrl(externalUriProperties.github())
                .build();
    }

    @Bean("botWebClient")
    public WebClient botWebClient() {
        return webClientBuilder().baseUrl(externalUriProperties.bot()).build();
    }

    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofMillis(timeout))))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(
                        HttpHeaders.USER_AGENT,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 YaBrowser/25.2.0.0 Safari/537.36");
    }
}
