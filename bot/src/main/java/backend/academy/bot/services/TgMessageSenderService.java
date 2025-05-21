package backend.academy.bot.services;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TgMessageSenderService {
    private final Logger log = LoggerFactory.getLogger(TgMessageSenderService.class);
    private final TelegramBot bot;

    public void sendMessageTG(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);

        bot.execute(sendMessage, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                log.info("sendMessageTG: {}", response);
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                log.error("Error sending message", e);
            }
        });
    }
}
