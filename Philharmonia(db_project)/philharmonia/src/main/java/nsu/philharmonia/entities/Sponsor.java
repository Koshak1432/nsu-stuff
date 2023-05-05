package nsu.philharmonia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "sponsor", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "surname"})})
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
        Sponsor sponsor = (Sponsor) o;
        return getId() != null && Objects.equals(getId(), sponsor.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}