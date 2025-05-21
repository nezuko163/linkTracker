package backend.academy.scrapper.data.repository.tracking.filter.impl;

import backend.academy.model.FilterModel;
import backend.academy.scrapper.data.database.jpa.entities.FilterEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.FilterEntityRepository;
import backend.academy.scrapper.data.repository.tracking.filter.FilterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class FilterRepositoryJpa implements FilterRepository {
    private final FilterEntityRepository filterEntityRepository;

    @Override
    public List<FilterModel> getAll() {
        return filterEntityRepository.findAll().stream()
                .map(filterEntity -> new FilterModel(filterEntity.id(), filterEntity.filter(), filterEntity.value()))
                .toList();
    }

    @Override
    public List<FilterModel> saveFilters(List<FilterModel> filters) {
        return filterEntityRepository
                .saveAllAndFlush(filters.stream()
                        .map(f -> new FilterEntity(f.filter(), f.value()))
                        .toList())
                .stream()
                .map(filterEntity -> new FilterModel(filterEntity.id(), filterEntity.filter(), filterEntity.value()))
                .toList();
    }
}
