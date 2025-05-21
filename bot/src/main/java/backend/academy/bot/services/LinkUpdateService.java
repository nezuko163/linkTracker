package backend.academy.bot.services;

import backend.academy.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final TgMessageSenderService service;

    public void update(LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().forEach(id -> service.sendMessageTG(id, linkUpdate.description()));
    }
}
