package nsu.philharmonia.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nsu.philharmonia.model.dto.buildings.BuildingDTO;

import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Setter
@Getter
public class PerformanceDTO {
    private Long id;
    private String name;
    private String typeName;
    private SponsorDTO sponsor;
    private BuildingDTO building;
    private Date performanceDate;
}
