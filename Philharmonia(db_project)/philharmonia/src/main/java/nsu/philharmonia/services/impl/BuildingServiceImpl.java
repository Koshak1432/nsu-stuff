package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.GenreDTO;
import nsu.philharmonia.model.dto.buildings.*;
import nsu.philharmonia.model.entities.Genre;
import nsu.philharmonia.model.entities.buildings.*;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.repositories.buildings.*;
import nsu.philharmonia.services.BuildingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingTypeRepository buildingTypeRepository;
    private final EstradeRepository estradeRepository;
    private final PalaceOfCultureRepository palaceOfCultureRepository;
    private final PerformanceVenueRepository performanceVenueRepository;
    private final TheaterRepository theaterRepository;
    private final ModelMapper mapper;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository, EstradeRepository estradeRepository,
                               PalaceOfCultureRepository palaceOfCultureRepository,
                               PerformanceVenueRepository performanceVenueRepository,
                               TheaterRepository theaterRepository, BuildingTypeRepository buildingTypeRepository, ModelMapper mapper) {
        this.buildingRepository = buildingRepository;
        this.estradeRepository = estradeRepository;
        this.palaceOfCultureRepository = palaceOfCultureRepository;
        this.performanceVenueRepository = performanceVenueRepository;
        this.theaterRepository = theaterRepository;
        this.buildingTypeRepository = buildingTypeRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<BuildingDTO> saveBuilding(BuildingDTO building) throws InvalidInputException {
        BuildingType type = buildingTypeRepository.findByName(building.getTypeName()).orElseThrow(
                () -> new InvalidInputException("Invalid building type name"));

        Building saved = buildingRepository.findById(building.getId()).map(b -> {
            b.setName(building.getName());
            b.setBuildingType(type);
            return buildingRepository.save(b);
        }).orElseGet(() -> {
            Building readyToMap = mapper.map(building, Building.class);
            readyToMap.setBuildingType(type);
            return buildingRepository.save(readyToMap);
        });
        return new ResponseEntity<>(mapper.map(saved, BuildingDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteBuilding(Long id) {
        buildingRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BuildingDTO>> getAll() {
        List<Building> buildings = (List<Building>) buildingRepository.findAll();
        List<BuildingDTO> buildingDTOS = buildings.stream().map(b -> mapper.map(b, BuildingDTO.class)).toList();
        return new ResponseEntity<>(buildingDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<EstradeDTO>> getEstrades() {
        List<Estrade> estrades = (List<Estrade>) estradeRepository.findAll();
        List<EstradeDTO> estradeDTOS = estrades.stream().map(e -> mapper.map(e, EstradeDTO.class)).toList();
        return new ResponseEntity<>(estradeDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TheaterDTO>> getTheaters() {
        List<Theater> theaters = (List<Theater>) theaterRepository.findAll();
        List<TheaterDTO> theaterDTOS = theaters.stream().map(e -> mapper.map(e, TheaterDTO.class)).toList();
        return new ResponseEntity<>(theaterDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PalaceOfCultureDTO>> getPalacesOfCulture() {
        List<PalaceOfCulture> palacesOfCulture = (List<PalaceOfCulture>) palaceOfCultureRepository.findAll();
        List<PalaceOfCultureDTO> palaceOfCultureDTOS = palacesOfCulture.stream().map(
                e -> mapper.map(e, PalaceOfCultureDTO.class)).toList();
        return new ResponseEntity<>(palaceOfCultureDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PerformanceVenueDTO>> getPerformanceVenues() {
        List<PerformanceVenue> performanceVenues = (List<PerformanceVenue>) performanceVenueRepository.findAll();
        List<PerformanceVenueDTO> performanceVenueDTOS = performanceVenues.stream().map(
                e -> mapper.map(e, PerformanceVenueDTO.class)).toList();
        return new ResponseEntity<>(performanceVenueDTOS, HttpStatus.OK);
    }
}
