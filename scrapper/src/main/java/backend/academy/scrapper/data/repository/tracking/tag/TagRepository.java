package backend.academy.scrapper.data.repository.tracking.tag;

import backend.academy.model.TagModel;
import java.util.List;

public interface TagRepository {
    List<TagModel> getAll();

    List<TagModel> saveTags(List<String> tags);
}
