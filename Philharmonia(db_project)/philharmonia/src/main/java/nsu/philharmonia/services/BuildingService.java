package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.buildings.*;
import nsu.philharmonia.model.entities.buildings.BuildingType;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BuildingService {
    ResponseEntity<List<BuildingDTO>> getAllBuildings();

    ResponseEntity<List<TheaterDTO>> getTheaters();
    ResponseEntity<List<PalaceOfCultureDTO>> getPalacesOfCulture();

    ResponseEntity<BuildingDTO> saveBuilding(BuildingDTO building) throws InvalidInputException;
    ResponseEntity<Void> deleteBuilding(Long id);

    ResponseEntity<Void> saveTheater(TheaterDTO theater);

    ResponseEntity<Void> savePalace(PalaceOfCultureDTO palace);

    ResponseEntity<Void> updatePalace(PalaceOfCultureDTO palace) throws NotFoundException;

    ResponseEntity<List<BuildingTypeDTO>> getBuildingTypes();
}
