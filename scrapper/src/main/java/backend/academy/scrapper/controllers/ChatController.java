package backend.academy.scrapper.controllers;

import backend.academy.StringConstants;
import backend.academy.scrapper.metrics.MessagesToServiceMetrics;
import backend.academy.scrapper.services.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RouterFunction;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = StringConstants.TG_CHAT_V1, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {
    private final MessagesToServiceMetrics metrics;
    private final ChatService chatService;

    Logger log = LoggerFactory.getLogger(ChatController.class);

    @PostMapping("/{id}")
    public ResponseEntity<String> registerChat(@PathVariable @Valid @Positive Long id) {
        chatService.registerChar(id);
        log.info("Зарегстрирован чат {}", id);
        metrics.incrementMessageCount();

        //        log.atInfo().setMessage("Зарегистрирован чат").addKeyValue("id", id).log();
        return ResponseEntity.ok("Чат " + id + " зарегистрирован");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable @Valid @Positive Long id) {
        String text;
        if (chatService.removeChat(id)) {
            text = String.format("Чат %d удалён", id);
        } else {
            text = String.format("Ошибка при удалении чата %d", id);
        }
        //        log.atInfo().setMessage(text).addKeyValue("id", id).log();
        metrics.incrementMessageCount();
        log.info(text + " id - " + id);
        return ResponseEntity.ok(text);
    }
}
