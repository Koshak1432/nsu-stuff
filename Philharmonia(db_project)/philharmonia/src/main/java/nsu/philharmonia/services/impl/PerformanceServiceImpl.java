package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.ContestPlaceDTO;
import nsu.philharmonia.model.dto.GenreDTO;
import nsu.philharmonia.model.dto.PerformanceDTO;
import nsu.philharmonia.model.entities.*;
import nsu.philharmonia.model.entities.buildings.Building;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.repositories.ContestPlaceRepository;
import nsu.philharmonia.repositories.PerformanceRepository;
import nsu.philharmonia.repositories.PerformanceTypeRepository;
import nsu.philharmonia.repositories.SponsorRepository;
import nsu.philharmonia.repositories.buildings.BuildingRepository;
import nsu.philharmonia.services.PerformanceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PerformanceServiceImpl implements PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final ContestPlaceRepository distributionRepository;
    private final PerformanceTypeRepository performanceTypeRepository;
    private final SponsorRepository sponsorRepository;
    private final BuildingRepository buildingRepository;
    private final ModelMapper mapper;

    @Override
    public ResponseEntity<List<PerformanceDTO>> getAllContests() {
        List<Performance> contests = performanceRepository.findPerformancesByPerformanceTypeName("конкурс");
        List<PerformanceDTO> contestDTOS = contests.stream().map(
                contest -> mapper.map(contest, PerformanceDTO.class)).toList();
        return new ResponseEntity<>(contestDTOS, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<PerformanceDTO> savePerformance(PerformanceDTO performance) throws InvalidInputException {
        PerformanceType performanceType = performanceTypeRepository.findByName(performance.getTypeName()).orElseThrow(
                () -> new InvalidInputException("Invalid performance type"));
        Sponsor sponsor = sponsorRepository.findSponsorByNameAndSurname(performance.getSponsor().getName(),
                                                                        performance.getSponsor().getSurname()).orElseThrow(
                () -> new InvalidInputException("Invalid sponsor"));
        Building building = buildingRepository.findBuildingByName(performance.getBuilding().getName()).orElseThrow(
                () -> new InvalidInputException("Invalid building"));

        Performance saved = performanceRepository.findById(performance.getId()).map(p -> {
            p.setName(performance.getName());
            p.setSponsor(sponsor);
            p.setPerformanceType(performanceType);
            p.setBuilding(building);
            p.setPerformanceDate(performance.getPerformanceDate());
            return performanceRepository.save(p);
        }).orElseGet(() -> {
            Performance newPerformance = mapper.map(performance, Performance.class);
            newPerformance.setPerformanceType(performanceType);
            newPerformance.setSponsor(sponsor);
            newPerformance.setBuilding(building);
            return performanceRepository.save(newPerformance);
        });
        return new ResponseEntity<>(mapper.map(saved, PerformanceDTO.class), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deletePerformance(Long id) {
        performanceRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ContestPlaceDTO>> getDistribution() {
        List<ContestPlace> contestPlaces = (List<ContestPlace>) distributionRepository.findAll();
        List<ContestPlaceDTO> contestPlaceDTOS = contestPlaces.stream().map(
                contest -> mapper.map(contest, ContestPlaceDTO.class)).toList();
        return new ResponseEntity<>(contestPlaceDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ContestPlaceDTO>> getDistributionByContestId(Long id) {
        List<ContestPlace> contestPlaces = (List<ContestPlace>) distributionRepository.findContestPlaceByPerformanceId(
                id);
        List<ContestPlaceDTO> contestPlaceDTOS = contestPlaces.stream().map(
                contest -> mapper.map(contest, ContestPlaceDTO.class)).toList();
        return new ResponseEntity<>(contestPlaceDTOS, HttpStatus.OK);
    }

    @Autowired
    public PerformanceServiceImpl(PerformanceRepository performanceRepository, ModelMapper mapper,
                                  ContestPlaceRepository distributionRepository,
                                  PerformanceTypeRepository performanceTypeRepository,
                                  SponsorRepository sponsorRepository, BuildingRepository buildingRepository) {
        this.performanceRepository = performanceRepository;
        this.mapper = mapper;
        this.distributionRepository = distributionRepository;
        this.performanceTypeRepository = performanceTypeRepository;
        this.sponsorRepository = sponsorRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    public ResponseEntity<List<PerformanceDTO>> getAll() {
        List<Performance> performances = (List<Performance>) performanceRepository.findAll();
        List<PerformanceDTO> performanceDTOS = performances.stream().map(
                p -> mapper.map(p, PerformanceDTO.class)).toList();
        return new ResponseEntity<>(performanceDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PerformanceDTO> getById(Long id) throws NotFoundException {
        Performance performance = findPerformance(id);
        PerformanceDTO performanceDTO = mapper.map(performance, PerformanceDTO.class);
        return new ResponseEntity<>(performanceDTO, HttpStatus.OK);
    }

    private Performance findPerformance(Long id) throws NotFoundException {
        Optional<Performance> performanceOptional = performanceRepository.findById(id);
        if (performanceOptional.isEmpty()) {
            throw new NotFoundException("Couldn't find performance");
        }
        return performanceOptional.get();
    }
}
