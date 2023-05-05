package nsu.philharmonia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "contest_place", uniqueConstraints = @UniqueConstraint(columnNames = {"place", "performance_id"}))
public class ContestPlace {

    @ToString.Include
    @EmbeddedId
    private ContestPlaceKey contestPlaceKey;

    @ToString.Exclude
    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ToString.Exclude
    @MapsId("performanceId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Positive
    @NotNull
    @ToString.Include
    @Column(name = "place")
    private Integer place;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ContestPlace that = (ContestPlace) o;
        return getContestPlaceKey() != null && Objects.equals(getContestPlaceKey(), that.getContestPlaceKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(contestPlaceKey);
    }
}