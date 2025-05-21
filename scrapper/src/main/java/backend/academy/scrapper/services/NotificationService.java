package backend.academy.scrapper.services;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.data.repository.chats.chatStorage.ChatStorageRepository;
import backend.academy.scrapper.data.repository.tracking.linkRelations.RelationRepository;
import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.scrapper.messaging.service.MessagingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MessagingService messagingService;
    private final ChatStorageRepository chatStorageRepository;
    private final RelationRepository relationRepository;

    public void processUpdate(CustomLink link, NotificationModel notificationModel) {
        log.info("processUpdate: link - {}", link);
        List<Long> chats = chatStorageRepository.getTgChatsTrackingLink(link.id());

        chats = chats.stream()
                .filter(chatId -> {
                    var filters = relationRepository.getFiltersByChatIdAndLinkId(chatId, link.id());
                    var user = filters.stream()
                            .filter(val -> val.value().equals("user"))
                            .findFirst();
                    if (user.isEmpty()) {
                        return true;
                    }
                    log.info("user - {}, notificUser - {}", user, notificationModel.user());
                    return user.get().value().equals(notificationModel.user());
                })
                .toList();
        messagingService.sendUpdate(new LinkUpdate(link.id(), link.url(), notificationModel.description(), chats));
    }
}
