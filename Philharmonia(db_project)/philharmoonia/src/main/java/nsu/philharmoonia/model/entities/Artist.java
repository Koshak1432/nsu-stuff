package nsu.philharmoonia.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

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
public class Artist {
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
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "artist_to_genre", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns =
    @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "artist_to_impresario", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns =
    @JoinColumn(name = "impresario_id"))
    private Set<Impresario> impresarios = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "performance", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name =
            "id"))
    private Set<Performance> performances = new LinkedHashSet<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
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