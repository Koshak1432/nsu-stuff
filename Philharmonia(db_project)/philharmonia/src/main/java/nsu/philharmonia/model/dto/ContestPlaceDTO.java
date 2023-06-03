package nsu.philharmonia.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class ContestPlaceDTO {
    private IdKeyDTO id;
    private Integer place;
}
