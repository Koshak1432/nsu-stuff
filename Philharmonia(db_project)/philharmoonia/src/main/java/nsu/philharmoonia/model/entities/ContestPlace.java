package nsu.philharmoonia.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "contest_place")
public class ContestPlace {

    @ToString.Include
    @EmbeddedId
    private ContestPlaceKey contestPlaceKey;

    @ToString.Exclude
    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ToString.Exclude
    @MapsId("performanceId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Positive
    @NotNull
    @ToString.Include
    @Column(name = "place")
    private Integer place;

}