package backend.academy.scrapper.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ScrapperProperties(
        @NotEmpty String githubToken, StackOverflowCredentials stackOverflow, Integer itemsOnPage) {
    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}
}
