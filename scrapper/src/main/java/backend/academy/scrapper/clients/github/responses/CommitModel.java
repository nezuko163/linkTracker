package backend.academy.scrapper.clients.github.responses;

import backend.academy.scrapper.domain.model.NotificationModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CommitModel(
        String sha,
        Commit commit,
        UserModel author,
        @JsonProperty("html_url") String url,
        @JsonProperty("comments_url") String commentsUrl) {
    private record Commit(String message) {}

    public NotificationModel toNotification() {
        return new NotificationModel(
                author.login(),
                String.format(
                        "Пользователь %s оставил коммит с сообщением: %s%n%s", author.login(), commit.message(), url));
    }

    @Override
    public int hashCode() {
        return sha.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof CommitModel commitModel) {
            return sha.equals(commitModel.sha());
        }
        return false;
    }
}
