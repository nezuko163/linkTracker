package backend.academy.scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.api")
public record ExternalUriProperties(String github, String so, String bot) {}
