package backend.academy.scrapper.data.repository.links.subscription.impl;

import backend.academy.StringConstants;
import backend.academy.dto.LinkResponse;
import backend.academy.exceptions.NotFoundError;
import backend.academy.scrapper.data.database.jpa.entityRepo.LinkEntityRepository;
import backend.academy.scrapper.data.repository.links.subscription.LinkSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class LinkSubscriptionRepositoryJpa implements LinkSubscriptionRepository {
    private final LinkEntityRepository linkEntityRepository;

    @Override
    public LinkResponse addSubscriber(String url) {
        var id = linkEntityRepository.getLinkEntityByUrlLikeIgnoreCase(url).id();
        return addSubscriber(id, url);
    }

    @Override
    public LinkResponse addSubscriber(Long id, String url) {
        linkEntityRepository.incrementSubscriber(url);
        return LinkResponse.of(id, url);
    }

    @Override
    public Boolean removeSubscriber(String link) {
        linkEntityRepository.decrementSubscriber(link);
        return true;
    }

    @Override
    public Boolean removeSubscriber(Long id) {
        var link = linkEntityRepository.findById(id).orElse(null);
        if (link == null) {
            throw new NotFoundError(StringConstants.cantFindLink(""));
        }
        return removeSubscriber(link.url());
    }
}
