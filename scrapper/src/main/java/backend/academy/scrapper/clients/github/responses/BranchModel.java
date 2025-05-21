package backend.academy.scrapper.clients.github.responses;

import backend.academy.scrapper.domain.model.NotificationModel;

public record BranchModel(String name, LastCommitModel commit) {
    public NotificationModel toNotification(String repo, String owner) {
        return new NotificationModel(owner, String.format("В репозитории %s была создана ветка: %s", repo, name));
    }
}
