package nsu.philharmonia.model.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class PerformanceDistributionDTO {
    private ArtistDTO artist;
    private PerformanceDTO performance;
    private IdKeyDTO id;
}
