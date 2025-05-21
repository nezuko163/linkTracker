package backend.academy.scrapper.data.database.jpa.entities;

import backend.academy.scrapper.domain.model.ObservableService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "links", schema = "links")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "last_checked_time")
    private Instant lastTimeObserve = Instant.now();

    @Column
    private Integer subscribers = 0;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ObservableService service;

    public static LinkEntity of(String url) {
        return of(url, null);
    }

    public static LinkEntity of(String url, Long id) {
        return new LinkEntity(id, url, Instant.now(), 0, ObservableService.NONE);
    }

    public static LinkEntity of(Long id) {
        return new LinkEntity(id, "", Instant.now(), 0, ObservableService.NONE);
    }
}
