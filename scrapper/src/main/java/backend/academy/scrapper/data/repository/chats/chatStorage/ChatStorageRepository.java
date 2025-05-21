package backend.academy.scrapper.data.repository.chats.chatStorage;

import java.util.List;

public interface ChatStorageRepository {

    List<Long> getTgChatsTrackingLink(String url);

    List<Long> getTgChatsTrackingLink(Long linkId);

    boolean isChatTrackingLink(Long linkId, Long tgChatId);
}
