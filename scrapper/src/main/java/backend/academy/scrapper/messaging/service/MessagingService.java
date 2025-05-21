package backend.academy.scrapper.messaging.service;

import backend.academy.dto.LinkUpdate;

public interface MessagingService {
    default void sendUpdate(LinkUpdate linkUpdate) {
        sendUpdate(linkUpdate, true);
    }
    ;

    void sendUpdate(LinkUpdate linkUpdate, boolean firstAttempt);
}
