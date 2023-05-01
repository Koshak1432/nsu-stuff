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
@Table(name = "impresario", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "surname"})})
public class Impresario {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToMany(mappedBy = "impresarios", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Artist> artists = new LinkedHashSet<>();

    @NotBlank
    @ToString.Include
    @Column(name = "name")
    private String name;

    @NotBlank
    @ToString.Include
    @Column(name = "surname")
    private String surname;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Impresario that = (Impresario) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}