package backend.academy.scrapper.clients.stackoverflow.async;

import backend.academy.scrapper.clients.stackoverflow.response.StackOverflowAnswersResponse;
import backend.academy.scrapper.clients.stackoverflow.response.StackOverflowCommentsResponse;
import backend.academy.scrapper.domain.model.ResponseWithHeadersForSingle;
import backend.academy.scrapper.properties.ScrapperProperties;
import backend.academy.scrapper.util.NetworkUtils;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class StackOverflowApiAsync {
    private static final String QUESTIONS = "questions/";
    private static final String ANSWERS = "answers/";
    private static final String FILTER = "!66dOUmaoptB09_0eyTxL*0djAXJJm4kDDK-wk2o4MluitRXhSYgAmZHJSzq";
    private static final Logger log = LoggerFactory.getLogger(StackOverflowApiAsync.class);

    private final String SO_KEY;
    private final WebClient webClient;
    private final NetworkUtils networkUtils;

    public StackOverflowApiAsync(
            ScrapperProperties config, NetworkUtils networkUtils, @Qualifier("soWebClient") WebClient webClient) {
        SO_KEY = config.stackOverflow().key();
        this.webClient = webClient;
        this.networkUtils = networkUtils;
    }

    public Mono<StackOverflowAnswersResponse> getAnswers(Long questionId, Long from) {
        URI uri = uriBuilder("{url}{id}/answers", QUESTIONS, questionId, from);

        return request(HttpMethod.GET, uri, "Questions", questionId, StackOverflowAnswersResponse.class);
    }

    public Mono<StackOverflowCommentsResponse> getCommentsOnAnswer(Long answerId, Long from) {
        return getStackOverflowCommentsResponse(answerId, from, ANSWERS);
    }

    public Mono<StackOverflowCommentsResponse> getCommentsOnQuestion(Long questionId, Long from) {
        return getStackOverflowCommentsResponse(questionId, from, QUESTIONS);
    }

    private Mono<StackOverflowCommentsResponse> getStackOverflowCommentsResponse(Long id, Long from, String url) {
        URI uri = uriBuilder("{url}{id}/comments", url, id, from);

        return request(HttpMethod.GET, uri, "Comments", id, StackOverflowCommentsResponse.class);
    }

    private <T> Mono<T> request(HttpMethod method, URI url, String post, Long id, Class<T> clazz) {
        try {
            return networkUtils
                    .executeRequestAsync(webClient.method(method).uri(url), clazz)
                    .map(ResponseWithHeadersForSingle::body);
        } catch (Exception e) {
            log.atError()
                    .setMessage("Ошибка при получении stackoverflow")
                    .addKeyValue("error", e.getMessage())
                    .addKeyValue("url", url)
                    .log();
            return Mono.error(e);
        }
    }

    private URI uriBuilder(String pattern, String url, Long id, Long from) {
        return UriComponentsBuilder.fromPath(pattern)
                .queryParam("order", "desc")
                .queryParam("sort", "creation")
                .queryParam("site", "stackoverflow")
                .queryParam("fromdate", from)
                .queryParam("filter", FILTER)
                .queryParam("key", SO_KEY)
                .buildAndExpand(url, id)
                .toUri();
    }

    private void logError(String postType, Long id, String uri, Integer code, HttpHeaders headers, String body) {
        log.atError()
                .setMessage("Ошибка при запросе")
                .addKeyValue("post", postType)
                .addKeyValue("id", id)
                .addKeyValue("uri", uri)
                .addKeyValue("code", code)
                .addKeyValue("headers", headers)
                .addKeyValue("body", body)
                .log();
    }
}
