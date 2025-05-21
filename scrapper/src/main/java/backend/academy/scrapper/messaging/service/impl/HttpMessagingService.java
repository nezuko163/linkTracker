package backend.academy.scrapper.messaging.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.clients.botapi.BotApiModule;
import backend.academy.scrapper.messaging.service.FailureMessageSupport;
import backend.academy.scrapper.messaging.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service("http")
@RequiredArgsConstructor
public class HttpMessagingService implements MessagingService, FailureMessageSupport {
    private static final Logger log = LogManager.getLogger(HttpMessagingService.class);
    private final BotApiModule botApiModule;
    private final ApplicationContext applicationContext;

    @Override
    public void sendUpdate(LinkUpdate linkUpdate, boolean firstAttempt) {
        botApiModule
                .sendUpdates(linkUpdate)
                .hasElement()
                .map(isSuccess -> {
                    if (!isSuccess) {
                        log.error("Failed to send update");
                        if (firstAttempt) {
                            log.debug("Повторный отправка сообщения - {}", linkUpdate);
                            onFailureSendMessage(linkUpdate);
                        } else {
                            log.debug("Повторная отправка сообщения провалилась(");
                        }
                    }
                    return isSuccess;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @Override
    public void onFailureSendMessage(LinkUpdate linkUpdate) {
        try {
            MessagingService fallback = applicationContext.getBean(KafkaMessagingService.class);
            fallback.sendUpdate(linkUpdate, false);
        } catch (BeansException e) {
            log.error("Failed to send update", e);
        }
    }
}
