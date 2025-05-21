package backend.academy.scrapper.data.repository.clientsRepository.stackOverflow.impl;

import backend.academy.scrapper.data.database.jpa.entities.LinkEntity;
import backend.academy.scrapper.data.database.jpa.entities.StackOverflowAnswerEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.SOAnswerEntityRepo;
import backend.academy.scrapper.data.repository.clientsRepository.stackOverflow.StackOverflowDataRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class StackOverflowDataJpa implements StackOverflowDataRepository {
    private final SOAnswerEntityRepo repo;

    @Override
    public List<Long> getAnswersIdOnQuestion(Long linkId) {
        return repo.findByLinkId(linkId).stream()
                .map(StackOverflowAnswerEntity::answerId)
                .toList();
    }

    @Override
    @Modifying
    @Transactional
    public List<Long> addAnswersIdOnQuestion(Long linkId, List<Long> answerIds) {
        return repo
                .saveAllAndFlush(answerIds.stream()
                        .map(answerId -> new StackOverflowAnswerEntity(answerId, LinkEntity.of(linkId)))
                        .toList())
                .stream()
                .map(StackOverflowAnswerEntity::answerId)
                .toList();
    }

    @Override
    public Boolean isLinkChecked(Long linkId) {
        return repo.existsByLinkId(linkId);
    }
}
