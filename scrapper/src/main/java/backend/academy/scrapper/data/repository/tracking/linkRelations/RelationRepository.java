package backend.academy.scrapper.data.repository.tracking.linkRelations;

import backend.academy.dto.LinkResponse;
import backend.academy.model.FilterModel;
import backend.academy.model.TagModel;
import java.util.List;

public interface RelationRepository {

    List<LinkResponse> getTrackedLinksByChat(Long chatId);

    List<FilterModel> getFiltersByChatIdAndLinkId(Long tgChatId, Long linkId);

    List<TagModel> getTagsByChatIdAndLinkId(Long tgChatId, Long linkId);

    Long getRelationId(Long linkId, Long tgChatId);

    void __clear_forTest();
}
