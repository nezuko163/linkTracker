package backend.academy.bot.config;

import backend.academy.StringConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final Logger log = LogManager.getLogger(WebClientConfig.class);
    @Value("${external.api.scrapper}")
    String scrapperUrl;

    @Bean
    public WebClient webClient() {
        log.info("scrapper url - {}", scrapperUrl + StringConstants.API_V1);
        var webclient = WebClient.builder()
            .baseUrl(scrapperUrl + StringConstants.API_V1) // Базовый URL
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Заголовок Content-Type
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // Заголовок Accept
            .build();

        webclient.get().uri("links").exchangeToMono(body -> body.bodyToMono(String.class)).subscribe(res -> log.info("asd - {}", res));
        return webclient;
    }
}
