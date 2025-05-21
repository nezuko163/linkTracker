package backend.academy.scrapper.services;

import backend.academy.scrapper.data.repository.chats.tgChatStorage.TgChatStorageRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final TgChatStorageRepository tgChatStorageRepository;

    public boolean registerChar(Long chatId) {
        return tgChatStorageRepository.registerChat(chatId);
    }

    public boolean removeChat(Long id) {
        boolean isRegistered = tgChatStorageRepository.removeChat(id);
        if (!isRegistered) return false;
        tgChatStorageRepository.removeChat(id);
        return true;
    }

    public Set<Long> allChats() {
        return tgChatStorageRepository.registeredChats();
    }
}
