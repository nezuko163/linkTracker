package backend.academy.scrapper.data.repository.tracking.linkRelations.impl;

import backend.academy.dto.LinkResponse;
import backend.academy.model.FilterModel;
import backend.academy.model.TagModel;
import backend.academy.scrapper.data.database.jpa.entityRepo.FilterEntityRepository;
import backend.academy.scrapper.data.database.jpa.entityRepo.LinksWithChatsEntityRepository;
import backend.academy.scrapper.data.database.jpa.entityRepo.TagEntityRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.data.repository.tracking.linkRelations.RelationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class RelationRepositoryJpa implements RelationRepository {
    private final LinksWithChatsEntityRepository linksWithChatsEntityRepository;
    private final LinkStorageRepository linkStorageRepository;
    private final TagEntityRepository tagEntityRepository;
    private final FilterEntityRepository filterEntityRepository;

    @Override
    public List<LinkResponse> getTrackedLinksByChat(Long chatId) {
        return linksWithChatsEntityRepository.findAllByChatId(chatId).stream()
                .map(chat -> LinkResponse.of(chat.id(), chat.link().url()))
                .toList();
    }

    @Override
    public List<FilterModel> getFiltersByChatIdAndLinkId(Long tgChatId, Long linkId) {
        return linksWithChatsEntityRepository.getFiltersByChatId(getRelationId(linkId, tgChatId));
    }

    @Override
    public List<TagModel> getTagsByChatIdAndLinkId(Long tgChatId, Long linkId) {
        return linksWithChatsEntityRepository.getTagsByChatId(getRelationId(linkId, tgChatId));
    }

    @Override
    public Long getRelationId(Long linkId, Long tgChatId) {
        return linksWithChatsEntityRepository
                .findByChatIdAndLinkId(tgChatId, linkId)
                .id();
    }

    @Override
    public void __clear_forTest() {
        linkStorageRepository.clear();
        filterEntityRepository.deleteAll();
        tagEntityRepository.deleteAll();
    }
}
