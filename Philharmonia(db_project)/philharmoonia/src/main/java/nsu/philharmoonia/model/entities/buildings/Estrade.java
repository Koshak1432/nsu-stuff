package nsu.philharmoonia.model.entities.buildings;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "estrade")
public class Estrade {

    @ToString.Exclude
    @Id
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "id", nullable = false)
    private Building building;

    @NotNull
    @ToString.Include
    @Column(name = "scene_height_meters")
    private Integer sceneHeightMeters;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Estrade estrade = (Estrade) o;
        return getBuilding() != null && Objects.equals(getBuilding(), estrade.getBuilding());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}