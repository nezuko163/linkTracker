package backend.academy.scrapper.data.repository.chats.tgChatStorage;

import java.util.Set;

public interface TgChatStorageRepository {
    boolean registerChat(Long chatId);

    boolean removeChat(Long chatId);

    boolean isChatRegistered(Long chatId);

    Set<Long> registeredChats();

    void clear();
}
