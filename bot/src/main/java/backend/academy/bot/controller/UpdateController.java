package backend.academy.bot.controller;

import backend.academy.StringConstants;
import backend.academy.bot.services.TgMessageSenderService;
import backend.academy.dto.LinkUpdate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(StringConstants.UPDATES_V1)
public class UpdateController {
    private final TgMessageSenderService tgMessageSenderService;

    @PostMapping
    public ResponseEntity<String> update(@RequestBody @Valid LinkUpdate linkUpdate) {
        linkUpdate
                .tgChatIds()
                .forEach(chatId -> tgMessageSenderService.sendMessageTG(chatId, linkUpdate.description()));

        return ResponseEntity.ok("Обновление обработано");
    }
}
