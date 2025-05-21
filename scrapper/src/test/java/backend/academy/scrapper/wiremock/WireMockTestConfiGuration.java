package backend.academy.scrapper.wiremock;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(
        basePackages = {
            "backend.academy.scrapper.config", // WebClientConfig
            "backend.academy.scrapper.properties", // ScrapperProperties
            "backend.academy.scrapper.util", // NetworkUtils и др. утилиты
            "backend.academy.scrapper.filters"
        })
@Configuration
public class WireMockTestConfiGuration {}
