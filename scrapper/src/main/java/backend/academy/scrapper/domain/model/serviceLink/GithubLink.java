package backend.academy.scrapper.domain.model.serviceLink;

import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.ObservableService;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class GithubLink extends CustomLink {
    private final String owner;
    private final String repo;

    public GithubLink() {
        super();
        owner = null;
        repo = null;
    }

    public GithubLink(String url, String owner, String repo) {
        super(url);
        this.owner = owner;
        this.repo = repo;
    }

    @Override
    public ObservableService service() {
        return ObservableService.GITHUB;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, repo);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (owner == null || repo == null) return false;
        if (obj instanceof GithubLink link) {
            return owner.equals(link.owner) && repo.equals(link.repo);
        }
        return false;
    }
}
