package backend.academy.scrapper.data.repository.clientsRepository.github.impl;

import backend.academy.scrapper.data.database.jpa.entities.GhBranchEntity;
import backend.academy.scrapper.data.database.jpa.entities.GhIssueEntity;
import backend.academy.scrapper.data.database.jpa.entities.GhPrEntity;
import backend.academy.scrapper.data.database.jpa.entities.LinkEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.GhBranchEntityRepository;
import backend.academy.scrapper.data.database.jpa.entityRepo.GhIssueEntityRepository;
import backend.academy.scrapper.data.database.jpa.entityRepo.GhPrEntityRepository;
import backend.academy.scrapper.data.repository.clientsRepository.github.GhDataRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class GhDataRepositoryJpa implements GhDataRepository {
    private final GhBranchEntityRepository branchRepo;
    private final GhIssueEntityRepository issueRepo;
    private final GhPrEntityRepository prRepo;

    @Override
    public List<Long> getPrOnRepo(Long linkId) {
        return prRepo.findByLinkId(linkId).stream().map(GhPrEntity::pr).toList();
    }

    @Override
    public List<Long> addPrOnRepo(Long linkId, List<Long> prs) {
        return prRepo
                .saveAllAndFlush(prs.stream()
                        .map(answerId -> new GhPrEntity(answerId, LinkEntity.of(linkId)))
                        .toList())
                .stream()
                .map(GhPrEntity::pr)
                .toList();
    }

    @Override
    public List<Long> getIssuesOnRepo(Long linkId) {
        return issueRepo.findByLinkId(linkId).stream().map(GhIssueEntity::issue).toList();
    }

    @Override
    public List<Long> addIssuesOnRepo(Long linkId, List<Long> issues) {
        return issueRepo
                .saveAllAndFlush(issues.stream()
                        .map(answerId -> new GhIssueEntity(answerId, LinkEntity.of(linkId)))
                        .toList())
                .stream()
                .map(GhIssueEntity::issue)
                .toList();
    }

    @Override
    public List<String> getBranchesOnRepo(Long linkId) {
        return branchRepo.findByLinkId(linkId).stream()
                .map(GhBranchEntity::branch)
                .toList();
    }

    @Override
    public List<String> addBranchesOnRepo(Long linkId, List<String> branches) {
        return branchRepo
                .saveAllAndFlush(branches.stream()
                        .map(answerId -> new GhBranchEntity(answerId, LinkEntity.of(linkId)))
                        .toList())
                .stream()
                .map(GhBranchEntity::branch)
                .toList();
    }

    @Override
    public Boolean isLinkChecked(Long linkId) {
        return null;
    }
}
