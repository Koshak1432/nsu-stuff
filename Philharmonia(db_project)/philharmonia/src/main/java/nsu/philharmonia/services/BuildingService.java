package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.buildings.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BuildingService {
    ResponseEntity<List<BuildingDTO>> getAll();

    ResponseEntity<List<EstradeDTO>> getEstrades();
    ResponseEntity<List<TheaterDTO>> getTheaters();
    ResponseEntity<List<PalaceOfCultureDTO>> getPalacesOfCulture();
    ResponseEntity<List<PerformanceVenueDTO>> getPerformanceVenues();
}
