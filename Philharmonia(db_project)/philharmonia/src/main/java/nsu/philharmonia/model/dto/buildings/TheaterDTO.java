package nsu.philharmonia.model.dto.buildings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class TheaterDTO {
    private BuildingDTO building;
    private Integer capacity;
}
