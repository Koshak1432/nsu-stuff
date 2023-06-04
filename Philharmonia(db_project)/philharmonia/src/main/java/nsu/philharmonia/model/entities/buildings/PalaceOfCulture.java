package nsu.philharmonia.model.entities.buildings;

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
@Table(name = "palace_of_culture")
public class PalaceOfCulture {
    @Id
    @Column(name = "id")
    private Long id;

    @MapsId
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "id")
    private Building building;

    @NotNull
    @Positive
    @ToString.Include
    @Column(name = "floor_num")
    private Integer floorNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PalaceOfCulture that = (PalaceOfCulture) o;
        return getBuilding() != null && Objects.equals(getBuilding(), that.getBuilding());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}