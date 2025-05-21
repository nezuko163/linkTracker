package backend.academy.scrapper.controllers;


import backend.academy.StringConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusOutputFormat;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
public class MetricsController {

    private final PrometheusScrapeEndpoint prometheusScrapeEndpoint;

    @GetMapping("/metrics")
    public ResponseEntity<String> metrics() {
        var response = prometheusScrapeEndpoint.scrape(
            PrometheusOutputFormat.CONTENT_TYPE_004,
            null
        );

        return ResponseEntity
            .status(response.getStatus())
            .contentType(MediaType.TEXT_PLAIN)
            .body(new String(response.getBody()));
    }
}
