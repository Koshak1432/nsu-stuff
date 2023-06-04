package nsu.philharmonia.controllers;

import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.buildings.*;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.services.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH + "/buildings")
@Validated
public class BuildingController {
    private final BuildingService buildingService;

    @Autowired
    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<BuildingDTO>> getAll() {
        return buildingService.getAll();
    }

    @PostMapping
    public ResponseEntity<BuildingDTO> addBuilding(@RequestBody BuildingDTO building) throws InvalidInputException {
        return buildingService.saveBuilding(building);
    }

    @PutMapping
    public ResponseEntity<BuildingDTO> updateBuilding(@RequestBody BuildingDTO building) throws InvalidInputException {
        return buildingService.saveBuilding(building);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable("id") @Positive Long id) {
        return buildingService.deleteBuilding(id);
    }

    @GetMapping("/estrades")
    public ResponseEntity<List<EstradeDTO>> getEstrades() {
        return buildingService.getEstrades();
    }

    @GetMapping("/theaters")
    public ResponseEntity<List<TheaterDTO>> getTheaters() {
        return buildingService.getTheaters();
    }

    @GetMapping("/venues")
    public ResponseEntity<List<PerformanceVenueDTO>> getVenues() {
        return buildingService.getPerformanceVenues();
    }

    @GetMapping("/palaces")
    public ResponseEntity<List<PalaceOfCultureDTO>> getPalaces() {
        return buildingService.getPalacesOfCulture();
    }
}
