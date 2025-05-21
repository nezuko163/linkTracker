package backend.academy.scrapper.data.repository.chats.tgChatStorage.impl;

import backend.academy.scrapper.data.database.jpa.entities.TgChatEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.ChatEntityRepository;
import backend.academy.scrapper.data.repository.chats.tgChatStorage.TgChatStorageRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class TgChatStorageRepositoryJpa implements TgChatStorageRepository {
    private final ChatEntityRepository chatEntityRepository;

    @Override
    public boolean registerChat(Long chatId) {
        chatEntityRepository.save(new TgChatEntity(chatId));
        return true;
    }

    @Override
    public boolean removeChat(Long chatId) {
        chatEntityRepository.deleteById(chatId);
        return false;
    }

    @Override
    public boolean isChatRegistered(Long chatId) {
        try {
            return chatEntityRepository.findById(chatId).get().chatId().equals(chatId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Set<Long> registeredChats() {
        return chatEntityRepository.findAll().stream().map(TgChatEntity::chatId).collect(Collectors.toSet());
    }

    @Override
    public void clear() {
        chatEntityRepository.deleteAll();
    }
}
