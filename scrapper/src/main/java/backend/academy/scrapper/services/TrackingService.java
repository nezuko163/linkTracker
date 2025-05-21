package backend.academy.scrapper.services;

import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import java.util.List;

public interface TrackingService {
    LinkResponse track(Long tgChatId, String url, List<String> filters, List<String> tags);

    LinkResponse untrack(Long tgChatId, String url);

    ListLinksResponse getTrackingLinksByChat(Long tgChatId);

    void untrackAllByChat(Long tgChatId);
}
