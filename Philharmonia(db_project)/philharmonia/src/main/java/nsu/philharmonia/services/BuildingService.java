package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.buildings.BuildingDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BuildingService {
    ResponseEntity<List<BuildingDTO>> getAll();
}
