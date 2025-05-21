package backend.academy.scrapper.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// public interface CustomLink {
//    String getLink();
//    ObservableService getService();
//    Instant getLastCheckedDate();
//    void setLastCheckedDate(Instant lastCheckedDate);
//    Integer getSubscribers();
//    void setSubscribers(Integer subscribers);
//    Long getId();
//    void setId(Long id);
//
//    default int linkHashCode() {
//        return getLink().hashCode();
//    }
//
//    default boolean linkEquals(CustomLink link) {
//        if (link == null) return false;
//        return link.getLink().equals(getLink());
//    }
// }

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CustomLink {
    protected final String url;
    protected ObservableService service = null;
    protected Instant lastCheckedDate = null;
    protected Integer subscribers = null;
    protected Long id = null;

    public CustomLink() {
        this.url = null;
    }

    public CustomLink(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
