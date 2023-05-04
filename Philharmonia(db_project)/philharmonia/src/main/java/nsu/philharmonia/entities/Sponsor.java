package nsu.philharmonia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

}