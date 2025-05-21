package backend.academy.bot.bot;

import com.pengrad.telegrambot.model.BotCommand;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@RequiredArgsConstructor
public enum Command {
    START("/start", "Зарегистрировать чат"),
    LIST("/list", "Вывести список отслеживаемых ссылок"),
    TRACK("/track", "Начать отслеживать ссылку"),
    UNTRACK("/untrack", "Прекратить отслеживание ссылки"),
    HELP("/help", "Узнать команды"),
    DIGEST("/digest", "Настроить отправку уведомлений не сразу при обнаружении, а по настраиваемому расписанию"),
    NONE(null, null);

    private static final Logger log = LogManager.getLogger(Command.class);
    private final String command;
    private final String description;

    public static Command messageHandle(String text) {
        return Optional.ofNullable(COMMAND_MAP.get(text)).orElse(Command.NONE);
    }

    public static BotCommand toBotCommand(Command command) {
        return new BotCommand(command.command, command.description);
    }

    public static BotCommand[] getBotCommands() {
        var commands = Arrays.stream(values())
                .filter(command -> command != NONE)
                .map(Command::toBotCommand)
                .toArray(BotCommand[]::new);
        log.info("commands - {}", commands);
        return commands;
    }

    private static final Map<String, Command> COMMAND_MAP = Map.of(
            "/start", START,
            "/help", HELP,
            "/track", TRACK,
            "/untrack", UNTRACK,
            "/list", LIST,
            "/digest", DIGEST);
}
