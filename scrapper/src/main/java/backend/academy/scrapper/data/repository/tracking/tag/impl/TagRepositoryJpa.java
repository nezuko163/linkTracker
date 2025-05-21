package backend.academy.scrapper.data.repository.tracking.tag.impl;

import backend.academy.model.TagModel;
import backend.academy.scrapper.data.database.jpa.entities.TagEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.TagEntityRepository;
import backend.academy.scrapper.data.repository.tracking.tag.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class TagRepositoryJpa implements TagRepository {
    private final TagEntityRepository tagEntityRepository;

    @Override
    public List<TagModel> getAll() {
        return tagEntityRepository.findAll().stream()
                .map(tagEntity -> new TagModel(tagEntity.id(), tagEntity.tag()))
                .toList();
    }

    @Override
    public List<TagModel> saveTags(List<String> tags) {
        return tagEntityRepository
                .saveAllAndFlush(tags.stream().map(TagEntity::new).toList())
                .stream()
                .map(tagEntity -> new TagModel(tagEntity.id(), tagEntity.tag()))
                .toList();
    }
}
