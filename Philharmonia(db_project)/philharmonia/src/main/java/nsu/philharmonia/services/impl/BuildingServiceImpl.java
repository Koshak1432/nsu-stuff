package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.buildings.BuildingDTO;
import nsu.philharmonia.model.dto.buildings.BuildingTypeDTO;
import nsu.philharmonia.model.dto.buildings.PalaceOfCultureDTO;
import nsu.philharmonia.model.dto.buildings.TheaterDTO;
import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.entities.buildings.BuildingType;
import nsu.philharmonia.model.entities.buildings.PalaceOfCulture;
import nsu.philharmonia.model.entities.buildings.Theater;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.repositories.buildings.BuildingRepository;
import nsu.philharmonia.repositories.buildings.BuildingTypeRepository;
import nsu.philharmonia.repositories.buildings.PalaceOfCultureRepository;
import nsu.philharmonia.repositories.buildings.TheaterRepository;
import nsu.philharmonia.services.BuildingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingTypeRepository buildingTypeRepository;
    private final PalaceOfCultureRepository palaceOfCultureRepository;
    private final TheaterRepository theaterRepository;
    private final ModelMapper mapper;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository,
                               PalaceOfCultureRepository palaceOfCultureRepository, TheaterRepository theaterRepository,
                               BuildingTypeRepository buildingTypeRepository, ModelMapper mapper) {
        this.buildingRepository = buildingRepository;
        this.palaceOfCultureRepository = palaceOfCultureRepository;
        this.theaterRepository = theaterRepository;
        this.buildingTypeRepository = buildingTypeRepository;
        this.mapper = mapper;
    }


    @Override
    public ResponseEntity<List<BuildingTypeDTO>> getBuildingTypes() {
        List<BuildingType> types = (List<BuildingType>) buildingTypeRepository.findAll();
        List<BuildingTypeDTO> dtos = types.stream().map(
                (element) -> mapper.map(element, BuildingTypeDTO.class)).toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updatePalace(PalaceOfCultureDTO palace) throws NotFoundException {
        PalaceOfCulture palaceOfCulture = palaceOfCultureRepository.findById(
                mapper.map(palace.getBuilding(), Building.class)).orElseThrow(
                () -> new NotFoundException("Not found palace"));
        palaceOfCulture.setFloorNum(palace.getFloorNum());
        palaceOfCultureRepository.save(palaceOfCulture);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> savePalace(PalaceOfCultureDTO palaceDto) {
//        PalaceOfCulture saved = palaceOfCultureRepository.findByBuildingName(palace.getBuilding().getName()).map(p
//        -> {
//            p.setFloorNum(palace.getFloorNum());
//            p.getBuilding().setBuildingType(buildingTypeRepository.findByName("дворец культуры").get());
//            return palaceOfCultureRepository.save(p);
//        }).orElseGet(() -> palaceOfCultureRepository.save(mapper.map(palace, PalaceOfCulture.class)));
//        return new ResponseEntity<>(HttpStatus.OK);
//        PalaceOfCulture palaceOfCulture = mapper.map(palaceDto, PalaceOfCulture.class);
//        palaceOfCulture.
//        palaceOfCultureRepository.save(palaceOfCulture);
//        return new ResponseEntity<>(HttpStatus.OK);
        return null;
    }


    @Override
    @Transactional
    public ResponseEntity<Void> saveTheater(TheaterDTO theater) {
        Theater saved = theaterRepository.findByBuildingName(theater.getBuilding().getName()).map(t -> {
            t.setCapacity(theater.getCapacity());
            t.getBuilding().setBuildingType(buildingTypeRepository.findByName("театр").get());
            return theaterRepository.save(t);
        }).orElseGet(() -> theaterRepository.save(mapper.map(theater, Theater.class)));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<BuildingDTO> saveBuilding(BuildingDTO building) throws InvalidInputException {
        BuildingType type = buildingTypeRepository.findByName(building.getTypeName()).orElseThrow(
                () -> new InvalidInputException("Invalid building type name"));

        Building saved = buildingRepository.findById(building.getId()).map(b -> {
            b.setName(building.getName());
            b.setBuildingType(type);
            return buildingRepository.save(b);
        }).orElseGet(() -> {
            Building newBuilding = mapper.map(building, Building.class);
            newBuilding.setBuildingType(type);
            return buildingRepository.save(newBuilding);
        });
        return new ResponseEntity<>(mapper.map(saved, BuildingDTO.class), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteBuilding(Long id) {
        buildingRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BuildingDTO>> getAllBuildings() {
        List<Building> buildings = (List<Building>) buildingRepository.findAll();
        List<BuildingDTO> buildingDTOS = buildings.stream().map(b -> mapper.map(b, BuildingDTO.class)).toList();
        return new ResponseEntity<>(buildingDTOS, HttpStatus.OK);
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
}
