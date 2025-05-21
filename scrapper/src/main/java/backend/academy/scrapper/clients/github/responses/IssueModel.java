package backend.academy.scrapper.clients.github.responses;

import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record IssueModel(
        String title,
        String body,
        @JsonProperty("html_url") String url,
        Integer number,
        UserModel user,
        @JsonProperty("created_at") String timeCreated,
        @JsonProperty("updated_at") String timeStringUpdatedAt) {
    public Instant time() {
        return Instant.parse(timeStringUpdatedAt);
    }

    public NotificationModel toNotification(String repo) {
        return new NotificationModel(
                user.login(),
                String.format(
                        "В репозитории %s%n" + "Пользователь %s%n" + "оставил новый вопрос: %s%n" + "В %s%n" + "%s%n"
                                + "%s",
                        repo, user.login(), title, timeCreated, StringUtils.shortPreview(body), url));
    }
}
