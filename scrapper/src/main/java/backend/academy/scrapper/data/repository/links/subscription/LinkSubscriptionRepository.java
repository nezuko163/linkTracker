package backend.academy.scrapper.data.repository.links.subscription;

import backend.academy.dto.LinkResponse;

public interface LinkSubscriptionRepository {
    LinkResponse addSubscriber(String link);

    LinkResponse addSubscriber(Long id, String url);

    Boolean removeSubscriber(String link);

    Boolean removeSubscriber(Long id);
}
