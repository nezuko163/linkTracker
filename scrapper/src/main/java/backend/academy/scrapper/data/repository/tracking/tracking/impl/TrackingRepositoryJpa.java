package backend.academy.scrapper.data.repository.tracking.tracking.impl;

import backend.academy.StringConstants;
import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.database.jpa.entities.ChatEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.FilterEntityRepository;
import backend.academy.scrapper.data.database.jpa.entityRepo.LinkEntityRepository;
import backend.academy.scrapper.data.database.jpa.entityRepo.LinksWithChatsEntityRepository;
import backend.academy.scrapper.data.database.jpa.entityRepo.TagEntityRepository;
import backend.academy.scrapper.data.repository.tracking.tracking.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class TrackingRepositoryJpa implements TrackingRepository {
    private final LinksWithChatsEntityRepository repository;
    private final LinkEntityRepository linkEntityRepository;
    private final TagEntityRepository tagEntityRepository;
    private final FilterEntityRepository filterEntityRepository;

    @Override
    public Long saveLinkOnChat(Long linkId, Long tgChatId) {
        var link = linkEntityRepository.findById(linkId);
        if (link.isEmpty()) throw new NotFoundError(StringConstants.cantFindLinkInChat("id = " + link, tgChatId));
        return repository.save(ChatEntity.of(link.get(), tgChatId)).id();
    }

    @Override
    public void saveTagOnRelation(Long relationId, Long tagId) {
        tagEntityRepository.insertTagOnChat(relationId, tagId);
    }

    @Override
    public void saveFilterOnRelation(Long relationId, Long filterId) {
        filterEntityRepository.insertFilterOnChat(relationId, filterId);
    }

    @Override
    public void removeRelation(Long linkId, Long tgChatId) {
        repository.deleteByChatIdAndLinkId(tgChatId, linkId);
    }

    @Override
    public void removeRelation(String url, Long tgChatId) {
        var link = linkEntityRepository.getLinkEntityByUrlLikeIgnoreCase(url);
        if (link == null) throw new NotFoundError(StringConstants.cantFindLink(url));

        repository.deleteByChatIdAndLinkId(tgChatId, link.id());
    }

    @Override
    public void removeAllRelationsFromChat(Long tgChatId) {
        repository.deleteAllByChatId(tgChatId);
    }
}
