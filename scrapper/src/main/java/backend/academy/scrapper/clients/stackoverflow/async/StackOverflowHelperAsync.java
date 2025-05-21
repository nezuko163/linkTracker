package backend.academy.scrapper.clients.stackoverflow.async;

import backend.academy.scrapper.clients.stackoverflow.response.StackOverflowAnswer;
import backend.academy.scrapper.clients.stackoverflow.response.StackOverflowComment;
import backend.academy.scrapper.data.repository.clientsRepository.stackOverflow.StackOverflowDataRepository;
import backend.academy.scrapper.data.repository.links.timeManagment.LinkTimeRepository;
import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.scrapper.domain.model.serviceLink.StackOverflowLink;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StackOverflowHelperAsync {

    private final StackOverflowApiAsync stackOverflowApi;
    private final StackOverflowDataRepository soRepository;
    private final LinkTimeRepository linkTimeRepository;
    private final Logger log = LoggerFactory.getLogger(StackOverflowHelperAsync.class);

    private final Set<StackOverflowLink> linksForObserving = ConcurrentHashMap.newKeySet();

    public void observeLink(StackOverflowLink link) {
        if (!soRepository.isLinkChecked(link.id())) {
            log.debug("check answers - {}", checkAnswers(link, Instant.EPOCH).subscribe());
        }
        linksForObserving.add(link);
    }

    public Map<StackOverflowLink, Flux<NotificationModel>> checkUpdates() {
        var res = new HashMap<StackOverflowLink, Flux<NotificationModel>>();
        linksForObserving.forEach(link -> {
            var time = link.lastCheckedDate();
            linkTimeRepository.updateLastCheckedTime(Instant.now(), link);
            var flux = Flux.concat(
                    checkAnswers(link, time), checkCommentsOnQuestion(link, time), checkCommentsOnAnswer(link, time));
            res.put(link, flux);
        });
        linksForObserving.clear();
        return res;
    }

    public Flux<NotificationModel> checkAnswers(StackOverflowLink link, Instant time) {
        var answers = stackOverflowApi
                .getAnswers(link.questionId(), time.getEpochSecond())
                .flatMap(res -> {
                    if (res == null || res.items().isEmpty()) {
                        return Mono.empty();
                    }
                    soRepository.addAnswersIdOnQuestion(
                            link.id(),
                            res.items().stream()
                                    .map(StackOverflowAnswer::answerId)
                                    .toList());

                    return Mono.just(res.items().stream()
                            .map(StackOverflowAnswer::toNotification)
                            .toList());
                });

        return Flux.from(answers).flatMapIterable(mono -> mono);
    }

    public Flux<NotificationModel> checkCommentsOnAnswer(StackOverflowLink link, Instant time) {
        var comments = soRepository.getAnswersIdOnQuestion(link.id()).stream().map(id -> stackOverflowApi
                .getCommentsOnAnswer(1L, time.getEpochSecond())
                .map(res -> res.items().stream()
                        .map(StackOverflowComment::toNotification)
                        .toList()));

        return Flux.fromStream(comments).flatMap(mono -> mono).flatMapIterable(mono -> mono);
    }

    public Flux<NotificationModel> checkCommentsOnQuestion(StackOverflowLink link, Instant time) {
        var comments = stackOverflowApi
                .getCommentsOnQuestion(link.questionId(), time.getEpochSecond())
                .map(res -> res.items().parallelStream()
                        .map(StackOverflowComment::toNotification)
                        .toList());

        return Flux.from(comments).flatMapIterable(mono -> mono);
    }
}
