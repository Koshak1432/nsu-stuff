package nsu.philharmonia.entities.buildings;

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
    @Id
    @Column(name = "id")
    private Long id;

    @ToString.Exclude
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private Building building;

    @NotNull
    @ToString.Include
    @Column(name = "scene_height_centimeters")
    private Integer sceneHeightCentimeters;

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