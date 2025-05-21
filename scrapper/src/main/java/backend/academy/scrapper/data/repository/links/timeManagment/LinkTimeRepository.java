package backend.academy.scrapper.data.repository.links.timeManagment;

import backend.academy.scrapper.domain.model.CustomLink;
import java.time.Instant;

public interface LinkTimeRepository {
    CustomLink updateLastCheckedTime(Instant time, CustomLink link);
}
