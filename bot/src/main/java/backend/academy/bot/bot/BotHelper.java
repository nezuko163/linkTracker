package backend.academy.bot.bot;

import backend.academy.bot.handler.CommandHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SetMyCommands;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotHelper {
    private static final Logger log = LogManager.getLogger(BotHelper.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final TelegramBot bot;
    private final CommandHandler commandHandler;

    @EventListener(ApplicationReadyEvent.class)
    public void startListen() {
        bot.setUpdatesListener(
                updates -> {
                    log.info("Update received: {}", updates);
                    updates.forEach(update -> CompletableFuture.runAsync(
                            () -> {
                                Long id = update.message().chat().id();
                                String text = update.message().text();
                                commandHandler.handleCommand(id, text);
                            },
                            executor));
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                },
                e -> {
                    if (e.response() != null) {
                        log.error(e.getMessage());
                        log.error(e.response().errorCode());
                        log.error(e.response().description());
                    } else {
                        log.error("Error receiving update from Telegram Bot", e);
                    }
                });
        bot.execute(new SetMyCommands(Command.getBotCommands()));
    }
}
