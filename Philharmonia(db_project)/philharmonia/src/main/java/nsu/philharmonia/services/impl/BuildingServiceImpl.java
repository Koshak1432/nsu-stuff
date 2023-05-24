package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.buildings.BuildingDTO;
import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.repositories.buildings.BuildingRepository;
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
    private final ModelMapper mapper;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository, ModelMapper mapper) {
        this.buildingRepository = buildingRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<List<BuildingDTO>> getAll() {
        List<Building> buildings = (List<Building>) buildingRepository.findAll();
        List<BuildingDTO> buildingDTOS = buildings.stream()
                .map(b -> mapper.map(b, BuildingDTO.class))
                .toList();
        return new ResponseEntity<>(buildingDTOS, HttpStatus.OK);
    }
}
