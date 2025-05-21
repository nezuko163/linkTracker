package backend.academy.scrapper.data.repository.clientsRepository.github;

import java.util.List;

public interface GhDataRepository {
    List<Long> getPrOnRepo(Long linkId);

    List<Long> addPrOnRepo(Long linkId, List<Long> prs);

    List<Long> getIssuesOnRepo(Long linkId);

    List<Long> addIssuesOnRepo(Long linkId, List<Long> issues);

    List<String> getBranchesOnRepo(Long linkId);

    List<String> addBranchesOnRepo(Long linkId, List<String> branches);

    Boolean isLinkChecked(Long linkId);
}
