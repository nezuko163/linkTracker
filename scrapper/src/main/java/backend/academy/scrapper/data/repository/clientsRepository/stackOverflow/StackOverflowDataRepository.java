package backend.academy.scrapper.data.repository.clientsRepository.stackOverflow;

import java.util.List;

public interface StackOverflowDataRepository {
    List<Long> getAnswersIdOnQuestion(Long linkId);

    List<Long> addAnswersIdOnQuestion(Long linkId, List<Long> answerIds);

    Boolean isLinkChecked(Long linkId);
}
