package backend.academy.scrapper.clients.stackoverflow.response;

import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public record StackOverflowComment(
        StackOverflowOwner owner,
        String body,
        @JsonProperty("post_id") Long answerId,
        @JsonProperty("comment_id") Long commentId,
        @JsonProperty("creation_date") Long creationDate,
        String link) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StackOverflowComment com) {
            return commentId.equals(com.commentId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(commentId);
    }

    public NotificationModel toNotification() {
        var dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(creationDate), ZoneId.of("Europe/Moscow"));

        return new NotificationModel(
                owner.displayName(),
                String.format(
                        "Пользователь %s%" + "nоставил новый комментарий: %s%n" + "в %s%n" + "на вопрос %s",
                        owner.displayName(), StringUtils.shortPreview(body), dateTime, link));
    }
}
