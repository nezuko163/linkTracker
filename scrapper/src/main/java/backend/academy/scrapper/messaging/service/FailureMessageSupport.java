package backend.academy.scrapper.messaging.service;

import backend.academy.dto.LinkUpdate;

public interface FailureMessageSupport {
    void onFailureSendMessage(LinkUpdate linkUpdate);
}
