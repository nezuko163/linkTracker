package backend.academy.scrapper.data.repository.links.LinkStorage;

import backend.academy.Paginated;
import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.ObservableService;
import java.util.Set;

public interface LinkStorageRepository {
    CustomLink saveLink(String url);

    CustomLink findLinkByUrl(String url);

    // TODO: вынести в отдельный тест репо
    Set<CustomLink> __findAll_forTest();

    Paginated<CustomLink> findAllPaging(int page);

    Long findIdByUrl(String url);

    void clear();

    int pagesCount();

    long countLinksByService(ObservableService service);

    default int pagesCount(Long itemsCount, Integer pageSize) {
        var ostatok = (int) (itemsCount % pageSize);
        if (ostatok != 0) ostatok = 1;
        return (int) (itemsCount / pageSize) + ostatok;
    }
}
