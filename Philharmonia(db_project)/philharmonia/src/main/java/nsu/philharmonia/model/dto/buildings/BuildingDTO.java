package nsu.philharmonia.model.dto.buildings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class BuildingDTO {
    private Long id;
    private String name;
    private BuildingTypeDTO type;
}
// todo remove type, need here type name only
// map(building, buildingDTO): building.getType().getName(), buildingDTO::setTypeName()
// что-то такое