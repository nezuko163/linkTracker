package backend.academy.scrapper.clients.stackoverflow.response;

import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record StackOverflowAnswer(
        List<String> tags,
        StackOverflowOwner owner,
        String link,
        @JsonProperty("answer_id") Long answerId,
        @JsonProperty("creation_date") Long creationDate,
        String body) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StackOverflowAnswer answer) {
            return answerId.equals(answer.answerId);
        }
        return false;
    }

    public StackOverflowAnswer changeLink(String link) {
        return new StackOverflowAnswer(tags, owner, link, answerId, creationDate, body);
    }

    @Override
    public int hashCode() {
        return answerId.hashCode();
    }

    public NotificationModel toNotification() {
        var dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(creationDate), ZoneId.of("Europe/Moscow"));
        return new NotificationModel(
                owner.displayName(),
                String.format(
                        "Пользователь %s%" + "nоставил добавил ответ: %s%n" + "в %s%n" + "на вопрос %s",
                        owner.displayName(), StringUtils.shortPreview(body), dateTime, link));
    }
}
