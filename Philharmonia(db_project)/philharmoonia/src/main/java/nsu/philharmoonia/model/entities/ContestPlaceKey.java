package nsu.philharmoonia.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class ContestPlaceKey implements Serializable {
    @ToString.Include
    @Column(name = "performance_id")
    private Long performanceId;

    @ToString.Include
    @Column(name = "artist_id")
    private Long artistId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ContestPlaceKey that = (ContestPlaceKey) o;
        return getPerformanceId() != null && Objects.equals(getPerformanceId(),
                                                            that.getPerformanceId()) && getArtistId() != null && Objects.equals(
                getArtistId(), that.getArtistId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(performanceId, artistId);
    }
}