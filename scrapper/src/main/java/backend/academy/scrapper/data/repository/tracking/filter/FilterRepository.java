package backend.academy.scrapper.data.repository.tracking.filter;

import backend.academy.model.FilterModel;
import java.util.List;

public interface FilterRepository {
    List<FilterModel> getAll();

    List<FilterModel> saveFilters(List<FilterModel> filters);
}
