package nsu.philharmonia.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "contest_place", uniqueConstraints = @UniqueConstraint(columnNames = {"place", "performance_id"}))
public class ContestPlace implements Serializable {

    @ToString.Include
    @EmbeddedId
    private ContestPlaceKey contestPlaceKey;

    @ToString.Exclude
    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ToString.Exclude
    @MapsId("performanceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Positive
    @NotNull
    @ToString.Include
    @Column(name = "place")
    private Integer place;

}