package nsu.philharmoonia.model.entities.buildings;

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
@Table(name = "performance_venue")
public class PerformanceVenue {
    @Id
    @Column(name = "id")
    private Long id;

    @ToString.Exclude
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private Building building;

    @Positive
    @NotNull
    private Integer area;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PerformanceVenue that = (PerformanceVenue) o;
        return getBuilding() != null && Objects.equals(getBuilding(), that.getBuilding());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}