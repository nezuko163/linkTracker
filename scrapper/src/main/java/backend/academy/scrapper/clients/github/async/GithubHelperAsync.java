package backend.academy.scrapper.clients.github.async;

import backend.academy.scrapper.clients.github.responses.CommitModel;
import backend.academy.scrapper.clients.github.responses.IssueCommentModel;
import backend.academy.scrapper.data.repository.links.timeManagment.LinkTimeRepository;
import backend.academy.scrapper.domain.model.NotificationModel;
import backend.academy.scrapper.domain.model.serviceLink.GithubLink;
import backend.academy.scrapper.util.ParseTime;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GithubHelperAsync {
    private static final Logger log = LogManager.getLogger(GithubHelperAsync.class);
    private final GithubApiAsync githubApi;
    private final LinkTimeRepository linkTimeRepository;

    private final Set<GithubLink> linksForObserving = ConcurrentHashMap.newKeySet();

    // repo -> branches
    private final Map<GithubLink, Set<String>> repoWithBranches = new ConcurrentHashMap<>();

    // repo -> issues
    private final Map<GithubLink, Set<Integer>> repoWithIssues = new ConcurrentHashMap<>();

    // repo -> pull requests
    private final Map<GithubLink, Set<Integer>> repoWithPR = new ConcurrentHashMap<>();

    public void observeLink(GithubLink link) {
        linksForObserving.add(link);
    }

    public Map<GithubLink, Flux<NotificationModel>> checkUpdates() {
        var res = new HashMap<GithubLink, Flux<NotificationModel>>();

        linksForObserving.forEach(link -> {
            Instant time = link.lastCheckedDate();
            linkTimeRepository.updateLastCheckedTime(Instant.now(), link);
            res.put(link, Flux.concat(checkBranches(time, link), checkIssues(link, time), checkPullRequests(link)));
        });
        linksForObserving.clear();
        log.debug("Обновления GitHub - {}", res);
        return res;
    }

    public Flux<NotificationModel> checkBranches(Instant time, GithubLink link) {
        String formatTime = ParseTime.parseTime(time);

        return githubApi.getBranchesOfRepo(link.owner(), link.repo()).flatMap(branch -> {
            var commits = checkCommits(branch.name(), link, formatTime);
            var a = repoWithBranches.computeIfAbsent(link, k -> new HashSet<>());
            Flux<NotificationModel> res = Flux.empty();
            if (!a.contains(branch.name())) {
                a.add(branch.name());
                res = Flux.concat(res, Flux.just(branch.toNotification(link.repo(), link.owner())));
            }
            return res;
        });
    }

    public Flux<NotificationModel> checkPullRequests(GithubLink link) {
        return githubApi.getPullRequestsOfRepo(link.owner(), link.repo()).flatMap(pr -> {
            var a = repoWithPR.computeIfAbsent(link, k -> ConcurrentHashMap.newKeySet());
            if (!a.contains(pr.number())) {
                a.add(pr.number());
                return Mono.just(pr.toNotification(link.repo()));
            }
            return Mono.empty();
        });
    }

    public Flux<NotificationModel> checkCommits(String branch, GithubLink link, String time) {
        return githubApi
                .getCommitsOfBranch(link.owner(), link.repo(), branch, time)
                .map(CommitModel::toNotification);
    }

    public Flux<NotificationModel> checkIssues(GithubLink link, Instant time) {
        return githubApi.getIssuesOfRepo(link.owner(), link.repo()).flatMap(issue -> {
            log.info("issue - {}", issue);
            var res = checkCommentsOfIssue(link, time, issue.number());
            var a = repoWithIssues.computeIfAbsent(link, k -> new HashSet<>());
            if (!a.contains(issue.number()) && issue.time().isAfter(time)) {
                res = Flux.concat(Flux.just(issue.toNotification(link.repo())), res);
                a.add(issue.number());
            }
            return res;
        });
    }

    public Flux<NotificationModel> checkCommentsOfIssue(GithubLink link, Instant time, Integer number) {
        return githubApi
                .getCommentsOfIssue(link.owner(), link.repo(), number, time)
                .map(IssueCommentModel::toNotification);
    }
}
