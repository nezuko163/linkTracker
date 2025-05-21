package backend.academy.scrapper.data.repository.chats.chatStorage.impl;

import backend.academy.scrapper.data.database.jpa.entities.ChatEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.LinksWithChatsEntityRepository;
import backend.academy.scrapper.data.repository.chats.chatStorage.ChatStorageRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class ChatStorageRepositoryJpa implements ChatStorageRepository {
    private final LinksWithChatsEntityRepository linksWithChatsEntityRepository;
    private final LinkStorageRepository linkStorageRepository;

    @Override
    public List<Long> getTgChatsTrackingLink(String url) {
        var id = linkStorageRepository.findIdByUrl(url);
        return getTgChatsTrackingLink(id);
    }

    @Override
    public List<Long> getTgChatsTrackingLink(Long linkId) {
        return linksWithChatsEntityRepository.findByLinkId(linkId).stream()
                .map(ChatEntity::chatId)
                .toList();
    }

    @Override
    public boolean isChatTrackingLink(Long linkId, Long tgChatId) {
        return linksWithChatsEntityRepository.findByChatIdAndLinkId(tgChatId, linkId) != null;
    }
}
