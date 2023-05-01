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
@Table(name = "theater")
public class Theater {

    @ToString.Exclude
    @Id
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "id", nullable = false)
    private Building building;

    @Column(name = "capacity")
    @Positive
    @NotNull
    private Integer capacity;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Theater theater = (Theater) o;
        return getBuilding() != null && Objects.equals(getBuilding(), theater.getBuilding());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}