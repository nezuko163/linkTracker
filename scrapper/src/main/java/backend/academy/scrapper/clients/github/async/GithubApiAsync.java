package backend.academy.scrapper.clients.github.async;

import backend.academy.scrapper.clients.github.responses.BranchModel;
import backend.academy.scrapper.clients.github.responses.CommitModel;
import backend.academy.scrapper.clients.github.responses.IssueCommentModel;
import backend.academy.scrapper.clients.github.responses.IssueModel;
import backend.academy.scrapper.clients.github.responses.PullRequestModel;
import backend.academy.scrapper.domain.model.ResponseWithHeadersForArray;
import backend.academy.scrapper.util.NetworkUtils;
import backend.academy.scrapper.util.ParseTime;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GithubApiAsync {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(GithubApiAsync.class);
    private final WebClient webClient;
    private final NetworkUtils networkUtils;

    public GithubApiAsync(@Qualifier("ghWebClient") WebClient webClient, NetworkUtils networkUtils) {
        this.webClient = webClient;
        this.networkUtils = networkUtils;
    }

    public Flux<BranchModel> getBranchesOfRepo(String owner, String repo) {
        var uri = UriComponentsBuilder.fromPath("{owner}/{repo}/branches")
                .buildAndExpand(owner, repo)
                .toString();

        return processRequest(request(HttpMethod.GET, uri, owner, repo, BranchModel.class))
            .filter(branch -> branch.name() != null);
    }

    public Flux<CommitModel> getCommitsOfBranch(String owner, String repo, String branchSHA, String sinceTime) {
        var uri = UriComponentsBuilder.fromPath("{owner}/{repo}/commits")
                .queryParam("sha", branchSHA)
                .queryParam("since", sinceTime)
                .buildAndExpand(owner, repo)
                .toString();

        return processRequest(request(HttpMethod.GET, uri, owner, repo, CommitModel.class))
            .filter(commit -> commit.url() != null);
    }

    public Flux<PullRequestModel> getPullRequestsOfRepo(String owner, String repo) {
        var uri = UriComponentsBuilder.fromPath("{owner}/{repo}/pulls")
                .buildAndExpand(owner, repo)
                .toString();

        return processRequest(request(HttpMethod.GET, uri, owner, repo, PullRequestModel.class))
            .filter(pullRequest -> pullRequest.url() != null);
    }

    public Flux<IssueModel> getIssuesOfRepo(String owner, String repo) {
        var uri = UriComponentsBuilder.fromPath("{owner}/{repo}/issues")
                .queryParam("per_page", 100)
                .queryParam("state", "open")
                .queryParam("sort", "created")
                .queryParam("direction", "asc")
                .buildAndExpand(owner, repo)
                .toString();
        return requestWithPaging(HttpMethod.GET, uri, owner, repo, IssueModel.class)
            .filter(issue -> issue.url() != null);
    }

    public Flux<IssueCommentModel> getCommentsOfIssue(String owner, String repo, Integer issueNumber, Instant since) {
        var uri = UriComponentsBuilder.fromPath("{user}/{repo}/issues/{issue_number}/comments")
                .queryParam("per_page", 100)
                .queryParam("since", ParseTime.parseTime(since))
                .buildAndExpand(owner, repo, issueNumber)
                .toString();
        return requestWithPaging(HttpMethod.GET, uri, owner, repo, IssueCommentModel.class)
            .filter(comment -> comment.url() != null);
    }

    private <T> Flux<T> requestWithPaging(HttpMethod method, String uri, String owner, String repo, Class<T> clazz) {
        return Flux.defer(() -> request(method, uri, owner, repo, clazz))
                .expand(request -> {
                    List<String> linkHeader = request.headers().get("Link");
                    if (linkHeader == null) return Mono.empty();
                    var nextPageUrl = extractNextPageUrl(linkHeader);
                    return request(HttpMethod.GET, nextPageUrl, owner, repo, clazz);
                })
                .flatMap(request -> Flux.fromIterable(request.body()));
    }

    private <T> Mono<ResponseWithHeadersForArray<T>> request(
            HttpMethod method, String url, String owner, String repo, Class<T> clazz) {
        try {
            return networkUtils.executeRequestAsyncWithHeadersForArray(
                    webClient.method(method).uri(url), clazz);
        } catch (Exception e) {
            log.error("Ошибка при получении GitHub");
            return Mono.error(e);
        }
    }

    private <T> Flux<T> processRequest(Mono<ResponseWithHeadersForArray<T>> request) {
        return request.flatMapMany(data -> {
            if (data.status().isError()) {
                return Flux.empty();
            }
            return Flux.fromIterable(data.body());
        });
    }

    private String extractNextPageUrl(List<String> linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return null;
        }

        String linkValue = linkHeader.getFirst(); // Link - один заголовок со множеством связей.
        Pattern pattern = Pattern.compile("<(.*?)>; rel=\"next\"");
        Matcher matcher = pattern.matcher(linkValue);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
