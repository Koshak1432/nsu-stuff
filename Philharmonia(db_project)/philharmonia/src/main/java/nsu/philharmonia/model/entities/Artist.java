package nsu.philharmonia.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
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
@Table(name = "artist", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "surname"}))
public class Artist implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "surname")
    private String surname;

    @ToString.Exclude
    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "artist_to_genre", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns =
    @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "artist_to_impresario", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns =
    @JoinColumn(name = "impresario_id"))
    private Set<Impresario> impresarios = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "artist_to_performance", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns = @JoinColumn(name =
            "performance_id"))
    private Set<Performance> performances = new LinkedHashSet<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<ContestPlace> contestPlaces = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Artist artist = (Artist) o;
        return getId() != null && Objects.equals(getId(), artist.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}