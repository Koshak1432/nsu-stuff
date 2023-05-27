package nsu.philharmonia.model.dto.buildings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class PalaceOfCultureDTO {
    private BuildingDTO building;
    private Integer floorNum;
}
