package backend.academy.scrapper.services;

import backend.academy.scrapper.clients.github.async.GithubHelperAsync;
import backend.academy.scrapper.clients.stackoverflow.async.StackOverflowHelperAsync;
import backend.academy.scrapper.data.repository.chats.chatStorage.ChatStorageRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.scrapper.domain.model.ObservableService;
import backend.academy.scrapper.domain.model.serviceLink.GithubLink;
import backend.academy.scrapper.domain.model.serviceLink.StackOverflowLink;
import backend.academy.scrapper.metrics.ActiveLinkMetrics;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class LinkObserverService {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(LinkObserverService.class);
    private final LinkStorageRepository linkStorageRepository;
    private final NotificationService notificationService;
    private final StackOverflowHelperAsync stackOverflowHelper;
    private final GithubHelperAsync githubHelper;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
    private final ActiveLinkMetrics activeLinkMetrics;

    @Scheduled(fixedDelay = 5000)
    @SuppressWarnings({"UnusedPrivateMethod", "UnusedMethod", "PMD.UnusedPrivateMethod"})
    @SuppressFBWarnings({"UnusedPrivateMethod", "UnusedMethod"})
    public void observeLinks() {
        int pagesCount = linkStorageRepository.pagesCount();
        activeLinkMetrics.updateLinkCount(ObservableService.GITHUB, linkStorageRepository.countLinksByService(ObservableService.GITHUB));
        activeLinkMetrics.updateLinkCount(ObservableService.STACK_OVERFLOW, linkStorageRepository.countLinksByService(ObservableService.STACK_OVERFLOW));
        for (int i = 0; i < pagesCount; i++) {
            int finalI = i;
            completionService.submit(() -> processAsync(finalI), null);
        }
        handleResults(pagesCount);
    }

    private void processAsync(int page) {
        var links = linkStorageRepository.findAllPaging(page);
        log.info("Отслеживаемые ссылки - {}", links);
        links.forEach(link -> {
            switch (link.service()) {
                case STACK_OVERFLOW -> stackOverflowHelper.observeLink((StackOverflowLink) link);
                case GITHUB -> githubHelper.observeLink((GithubLink) link);
            }
        });
        checkUpdatesAsync().forEach((link, messages) -> messages
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(message -> {
                log.info("Получены новые сообщения с ссылки {}: {}", link, message);
                sendUpdateToUsers(link, message);
            }));
    }

    private void handleResults(int taskCount) {
        try {
            for (int i = 0; i < taskCount; i++) {
                Future<Void> future = completionService.take();
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Ошибка в обработке ссылок", e);
        }
    }

    public Map<CustomLink, Flux<NotificationModel>> checkUpdatesAsync() {
        var res = new ConcurrentHashMap<CustomLink, Flux<NotificationModel>>();
        res.putAll(stackOverflowHelper.checkUpdates());
        res.putAll(githubHelper.checkUpdates());

        return res;
    }

    private void sendUpdateToUsers(CustomLink link, NotificationModel notificationModel) {
        notificationService.processUpdate(link, notificationModel);
    }
}
