package nsu.philharmonia.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nsu.philharmonia.model.entities.buildings.Building;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "performance")
public class Performance implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @ToString.Include
    @Column(name = "name")
    private String name;

    @ToString.Exclude
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "type_id")
    private PerformanceType performanceType;

    @ToString.Exclude
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sponsor_id")
    private Sponsor sponsor;

    @ToString.Exclude
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "building_id")
    private Building building;

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(mappedBy = "performances", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Artist> artists = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContestPlace> contestPlaces = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Performance that = (Performance) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}