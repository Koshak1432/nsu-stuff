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
    private String typeName;
}