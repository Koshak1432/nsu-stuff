package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.buildings.*;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BuildingService {
    ResponseEntity<List<BuildingDTO>> getAll();

    ResponseEntity<List<EstradeDTO>> getEstrades();
    ResponseEntity<List<TheaterDTO>> getTheaters();
    ResponseEntity<List<PalaceOfCultureDTO>> getPalacesOfCulture();
    ResponseEntity<List<PerformanceVenueDTO>> getPerformanceVenues();

    ResponseEntity<BuildingDTO> saveBuilding(BuildingDTO building) throws InvalidInputException;
    ResponseEntity<Void> deleteBuilding(Long id);
}
