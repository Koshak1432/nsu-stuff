package nsu.philharmonia.services.impl;

import nsu.philharmonia.model.dto.ContestPlaceDTO;
import nsu.philharmonia.model.dto.PerformanceDTO;
import nsu.philharmonia.model.entities.ContestPlace;
import nsu.philharmonia.model.entities.Performance;
import nsu.philharmonia.model.entities.PerformanceType;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.repositories.ContestPlaceRepository;
import nsu.philharmonia.repositories.PerformanceRepository;
import nsu.philharmonia.services.PerformanceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerformanceServiceImpl implements PerformanceService {
    private PerformanceRepository performanceRepository;
    private ContestPlaceRepository distributionRepository;
    private ModelMapper mapper;

    @Override
    public ResponseEntity<List<PerformanceDTO>> getAllContests() {
        List<Performance> contests = performanceRepository.findPerformancesByPerformanceTypeName("конкурс");
        List<PerformanceDTO> contestDTOS = contests.stream().map(
                contest -> mapper.map(contest, PerformanceDTO.class)).toList();
        return new ResponseEntity<>(contestDTOS, HttpStatus.OK);
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
        List<ContestPlace> contestPlaces = (List<ContestPlace>) distributionRepository.findContestPlaceByPerformanceId(id);
        List<ContestPlaceDTO> contestPlaceDTOS = contestPlaces.stream().map(
                contest -> mapper.map(contest, ContestPlaceDTO.class)).toList();
        return new ResponseEntity<>(contestPlaceDTOS, HttpStatus.OK);
    }

    @Autowired
    public PerformanceServiceImpl(PerformanceRepository performanceRepository, ModelMapper mapper,
                                  ContestPlaceRepository distributionRepository) {
        this.performanceRepository = performanceRepository;
        this.mapper = mapper;
        this.distributionRepository = distributionRepository;
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
