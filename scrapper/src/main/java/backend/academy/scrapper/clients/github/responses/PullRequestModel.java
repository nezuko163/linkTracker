package backend.academy.scrapper.clients.github.responses;

import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public record PullRequestModel(
        Integer number,
        String body,
        String title,
        String created_at,
        @JsonProperty("html_url") String url,
        @JsonProperty("user") UserModel user) {
    public NotificationModel toNotification(String repoUrl) {
        var dateTime = OffsetDateTime.parse(created_at);
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return new NotificationModel(
                user.login(),
                String.format(
                        "В репозитории %s%n" + "Пользователем %s%n"
                                + "был создан Pull Request: %s%n"
                                + "%s%n"
                                + "в %s%n"
                                + "%s%n",
                        repoUrl, user.login(), title, url, dateTime.format(formatter), StringUtils.shortPreview(body)));
    }
}
