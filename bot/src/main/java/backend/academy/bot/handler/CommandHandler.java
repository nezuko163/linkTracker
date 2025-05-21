package backend.academy.bot.handler;

import backend.academy.bot.bot.Command;
import backend.academy.bot.domain.model.StateMachine;
import backend.academy.bot.services.ScrapperService;
import backend.academy.bot.services.StateMachineService;
import backend.academy.bot.services.TgMessageSenderService;
import backend.academy.dto.LinkResponse;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final ScrapperService scrapperService;
    private final StateMachineService stateMachineService;
    private final TgMessageSenderService tgMessageSenderService;
    private final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    public void handleCommand(Long id, String text) {
        var command = Command.messageHandle(text);
        if (command != Command.NONE) {
            stateMachineService.clear(id);
        }
        switch (command) {
            case START -> startCommand(id);
            case LIST -> linkCommand(id);
            case HELP -> helpCommand(id);
            case TRACK -> trackCommand(id);
            case UNTRACK -> untrackCommand(id);
            case DIGEST -> {}
            case NONE -> handleCommand(stateMachineService.getStateMachine(id), id, text);
        }
    }

    protected void linkCommand(Long chatId) {
        scrapperService
                .listLinks(chatId)
                .subscribe(
                        result -> {
                            log.debug("отправка списка ссылок");
                            tgMessageSenderService.sendMessageTG(chatId, formatLinksList(result));
                        },
                        error -> {
                            log.error(error.getMessage(), error);
                        });
    }

    protected void trackCommand(Long chatId) {
        logState(stateMachineService
                .setEvent(chatId, StateMachine.Event.START_TRACK)
                .state());
        tgMessageSenderService.sendMessageTG(chatId, "Введите ссылку");
    }

    protected void startCommand(Long chatId) {
        scrapperService
                .start(chatId)
                .subscribe(
                        result -> tgMessageSenderService.sendMessageTG(chatId, result),
                        error -> log.error(error.getMessage(), error));
    }

    protected void untrackCommand(Long chatId) {
        logState(stateMachineService
                .setEvent(chatId, StateMachine.Event.START_UNTRACKING_LINK)
                .state());
        tgMessageSenderService.sendMessageTG(chatId, "Введите ссылку");
    }

    protected void helpCommand(Long id) {
        tgMessageSenderService.sendMessageTG(id, "Команды: ");
    }

    protected void digestCommand(long chatId) {
        tgMessageSenderService.sendMessageTG(chatId, "Введите время отправления уведомлений");
    }

    protected void setDigestTime(long chatId, String time) {}

    protected void untrackLink(Long chatId, String url) {
        scrapperService.untrack(chatId, url).subscribe(result -> tgMessageSenderService.sendMessageTG(chatId, result));
    }

    private void handleCommand(StateMachine fsm, Long id, String text) {
        if (fsm == null || fsm.state() == StateMachine.State.NONE) {
            tgMessageSenderService.sendMessageTG(id, "Неверная команда");
        } else {
            logState(fsm.state());
            switch (fsm.state()) {
                case WAITING_LINK -> enterLink(id, text);
                case WAITING_FILTERS -> {
                    if (emptyTagFilter(text)) {
                        enterFilters(id, new String[0]);
                    } else {
                        enterFilters(id, text.strip().split(","));
                    }
                }
                case WAITING_TAGS -> {
                    if (emptyTagFilter(text)) {
                        enterTags(id, new String[0]);
                    } else {
                        enterTags(id, text.strip().split(" "));
                    }
                }
                case WAITING_DIGEST_TIME -> {
                    if (!(text == null || text.isEmpty())) {
                        enterTime(id, text);
                        fsm.setEvent(StateMachine.Event.CLOSE);
                    } else {
                        log.error("WAITING_DIGEST_TIME text = null");
                    }
                }

                case WAITING_UNTRACK_LINK -> {
                    if (!(text == null || text.isEmpty())) {
                        untrackLink(id, text);
                        fsm.setEvent(StateMachine.Event.CLOSE);
                    } else {
                        log.error("WAITING_UNTRACK_LINK text = null");
                    }
                }
                case NONE -> {
                    log.error("error");
                }
                case null -> {}
                default -> {}
            }
        }
    }

    private String formatLinksList(List<LinkResponse> list) {
        if (list.isEmpty()) {
            return "Нет отслеживаемых ссылок";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            log.info(list.get(i).toString());
            sb.append(i + 1).append(". ").append(list.get(i).url()).append("\n");
        }
        return sb.toString();
    }

    private void logState(StateMachine.State state) {
        log.atInfo().setMessage("State").addKeyValue("state", state).log();
    }

    protected void enterLink(Long chatId, String url) {
        stateMachineService.setUrl(chatId, url);
        tgMessageSenderService.sendMessageTG(
                chatId,
                "Введите фильтры разделённые запятыми (пример - user: nezuko163)\n(введите - если хотите без них)");
    }

    protected void enterFilters(Long chatId, @Nullable String[] filters) {
        stateMachineService.setFilters(chatId, filters);
        tgMessageSenderService.sendMessageTG(chatId, "Введите теги (введите - если хотите без них)");
    }

    protected void enterTags(Long chatId, @Nullable String[] tags) {
        var state = stateMachineService.setTags(chatId, tags);
        var resUrl = state.url();
        List<String> resTags;
        if (tags == null) {
            resTags = new ArrayList<>();
        } else {
            resTags = new ArrayList<>(Arrays.asList(tags));
        }
        List<String> resFilters;
        if (state.filters() == null) {
            resFilters = new ArrayList<>();
        } else {
            resFilters = new ArrayList<>(Arrays.asList(state.filters()));
        }
        log.info("filters - {}", resFilters);
        log.info("tags - {}", resTags);
        scrapperService
                .startTracking(chatId, resUrl, resTags, resFilters)
                .subscribe(result -> tgMessageSenderService.sendMessageTG(chatId, result));
        stateMachineService.setEvent(chatId, StateMachine.Event.CLOSE);
    }

    protected void enterTime(long chatId, String time) {
        var state = stateMachineService.setDigestTime(chatId, time);
    }

    private boolean emptyTagFilter(String text) {
        return text == null || text.isEmpty() || (text.length() == 1 && text.charAt(0) == '-');
    }
}
