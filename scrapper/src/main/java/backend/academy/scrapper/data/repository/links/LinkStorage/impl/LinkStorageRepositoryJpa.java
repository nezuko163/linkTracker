package backend.academy.scrapper.data.repository.links.LinkStorage.impl;

import backend.academy.Paginated;
import backend.academy.scrapper.data.database.jpa.entities.LinkEntity;
import backend.academy.scrapper.data.database.jpa.entityRepo.LinkEntityRepository;
import backend.academy.scrapper.data.database.jpa.mapper.LinkMapper;
import backend.academy.scrapper.data.database.jpa.mapper.PageMapper;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.ObservableService;
import backend.academy.scrapper.properties.ScrapperProperties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.access-type", havingValue = "orm")
public class LinkStorageRepositoryJpa implements LinkStorageRepository {
    private final ScrapperProperties config;
    private final LinkEntityRepository linkEntityRepository;

    @Override
    public CustomLink saveLink(String url) {
        var link = linkEntityRepository.saveAndFlush(LinkEntity.of(url));
        return LinkMapper.toCustomLink(link);
    }

    @Override
    public CustomLink findLinkByUrl(String url) {
        return LinkMapper.toCustomLink(linkEntityRepository.getLinkEntityByUrlLikeIgnoreCase(url));
    }

    @Override
    public Set<CustomLink> __findAll_forTest() {
        return linkEntityRepository.findAll().stream()
                .map(LinkMapper::toCustomLink)
                .collect(Collectors.toSet());
    }

    @Override
    public Paginated<CustomLink> findAllPaging(int page) {
        return PageMapper.fromPage(
                linkEntityRepository.findAll(PageRequest.of(page, config.itemsOnPage())), LinkMapper::toCustomLink);
    }

    @Override
    public Long findIdByUrl(String url) {
        return findLinkByUrl(url).id();
    }

    @Override
    public void clear() {
        linkEntityRepository.truncate();
    }

    @Override
    public int pagesCount() {
        return (int) (linkEntityRepository.count() / config.itemsOnPage());
    }

    @Override
    public long countLinksByService(ObservableService service) {
        return linkEntityRepository.countByService(service);
    }

}
