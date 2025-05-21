package backend.academy.scrapper.data.database.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "gh_branches", schema = "links")
public class GhBranchEntity {
    @Id
    @Column(name = "branch")
    private String branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_id", referencedColumnName = "id")
    private LinkEntity link;
}
