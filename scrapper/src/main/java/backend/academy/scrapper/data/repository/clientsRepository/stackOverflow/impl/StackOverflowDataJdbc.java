package backend.academy.scrapper.data.repository.clientsRepository.stackOverflow.impl;

import backend.academy.scrapper.data.repository.clientsRepository.stackOverflow.StackOverflowDataRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "sql")
public class StackOverflowDataJdbc implements StackOverflowDataRepository {
    private static final Logger log = LogManager.getLogger(StackOverflowDataJdbc.class);
    private final JdbcClient jdbcClient;

    @Override
    public List<Long> getAnswersIdOnQuestion(Long linkId) {
        return jdbcClient
                .sql(
                        """
                SELECT answer_id FROM links.so_answers
                WHERE link_id = :linkId
                """)
                .param("linkId", linkId)
                .query(Long.class)
                .list();
    }

    @Override
    public List<Long> addAnswersIdOnQuestion(Long linkId, List<Long> answerIds) {
        answerIds.forEach(answerId -> jdbcClient
                .sql(
                        """
                INSERT INTO links.so_answers
                (answer_id, link_id) VALUES (:answerId, :linkId)
                ON CONFLICT (answer_id)
                DO NOTHING
                """)
                .param("answerId", answerId)
                .param("linkId", linkId)
                .update());
        return answerIds;
    }

    @Override
    public Boolean isLinkChecked(Long linkId) {
        var res = jdbcClient
                .sql(
                        """
                SELECT COUNT(*) = 1 FROM links.so_answers
                WHERE link_id = :linkId
                """)
                .param("linkId", linkId)
                .query(Boolean.class)
                .single();
        log.debug("isLinkChecked: res - {}", res);
        return res;
    }
}
