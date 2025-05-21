package backend.academy.scrapper.services;

import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.exceptions.NotFoundError;
import backend.academy.model.FilterModel;
import backend.academy.model.TagModel;
import backend.academy.scrapper.data.repository.chats.chatStorage.ChatStorageRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.data.repository.links.subscription.LinkSubscriptionRepository;
import backend.academy.scrapper.data.repository.tracking.filter.FilterRepository;
import backend.academy.scrapper.data.repository.tracking.linkRelations.RelationRepository;
import backend.academy.scrapper.data.repository.tracking.tag.TagRepository;
import backend.academy.scrapper.data.repository.tracking.tracking.TrackingRepository;
import backend.academy.scrapper.domain.model.CustomLink;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {
    private static final Logger log = LogManager.getLogger(TrackingServiceImpl.class);
    private final LinkStorageRepository linkStorageRepository;
    private final LinkSubscriptionRepository linkSubscriptionRepository;
    private final ChatStorageRepository chatStorageRepository;
    private final FilterRepository filterRepository;
    private final TagRepository tagRepository;
    private final TrackingRepository trackingRepository;
    private final RelationRepository relationRepository;

    @Override
    @Transactional()
    public LinkResponse track(Long tgChatId, String url, List<String> filters, List<String> tags) {
        log.info("TrackingServiceImpl track: ");

        try {
            var link = linkStorageRepository.findLinkByUrl(url);
            log.info("link found");
            if (chatStorageRepository.isChatTrackingLink(link.id(), tgChatId)) {
                var linkFilters = relationRepository.getFiltersByChatIdAndLinkId(tgChatId, link.id());
                var linkTags = relationRepository.getTagsByChatIdAndLinkId(tgChatId, link.id());

                return new LinkResponse(
                        link.id(),
                        link.url(),
                        linkTags.stream().map(TagModel::toString).toList(),
                        linkFilters.stream().map(FilterModel::toString).toList());
            }

            _trackLinkByChat(tgChatId, link, filters, tags);
            return new LinkResponse(link.id(), link.url(), filters, tags);

        } catch (NotFoundError e) {
            var link = linkStorageRepository.saveLink(url);
            log.info("link not found");
            _trackLinkByChat(tgChatId, link, filters, tags);
            return new LinkResponse(link.id(), link.url(), tags, filters);
        }
    }

    private void _trackLinkByChat(Long tgChatId, CustomLink link, List<String> filters, List<String> tags) {
        var tagModels = tagRepository.saveTags(tags);
        var filterModels = filterRepository.saveFilters(filters.stream()
                .map(FilterModel::toFilter)
                .filter(Objects::nonNull)
                .toList());

        var relationChatId = trackingRepository.saveLinkOnChat(link.id(), tgChatId);

        filterModels.forEach(filterModel -> trackingRepository.saveFilterOnRelation(relationChatId, filterModel.id()));

        tagModels.forEach(tagModel -> trackingRepository.saveTagOnRelation(relationChatId, tagModel.id()));

        linkSubscriptionRepository.addSubscriber(link.id(), link.url());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public LinkResponse untrack(Long tgChatId, String url) {
        var linkId = linkStorageRepository.findIdByUrl(url);
        trackingRepository.removeRelation(url, tgChatId);
        linkSubscriptionRepository.removeSubscriber(linkId);

        return new LinkResponse(
                linkId,
                url,
                relationRepository.getTagsByChatIdAndLinkId(tgChatId, linkId).stream()
                        .map(TagModel::toString)
                        .toList(),
                relationRepository.getFiltersByChatIdAndLinkId(tgChatId, linkId).stream()
                        .map(FilterModel::toString)
                        .toList());
    }

    @Override
    public ListLinksResponse getTrackingLinksByChat(Long tgChatId) {
        var links = relationRepository.getTrackedLinksByChat(tgChatId);
        return new ListLinksResponse(links, links.size());
    }

    @Override
    public void untrackAllByChat(Long tgChatId) {
        trackingRepository.removeAllRelationsFromChat(tgChatId);
    }
}
