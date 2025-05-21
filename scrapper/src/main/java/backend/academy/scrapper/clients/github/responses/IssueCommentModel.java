package backend.academy.scrapper.clients.github.responses;

import backend.academy.scrapper.domain.model.NotificationModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public record IssueCommentModel(
        @JsonProperty("html_url") String url,
        String body,
        UserModel user,
        @JsonProperty("created_at") String timeCreatedAt) {

    public NotificationModel toNotification() {
        return new NotificationModel(
                user.login(),
                String.format(
                        "Пользователь %s оставил комментарий: %s" + "%n" + "на вопрос %s", user.login(), body, url));
    }
}
