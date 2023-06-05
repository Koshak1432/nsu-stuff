package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.*;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PerformanceService {
    ResponseEntity<List<PerformanceDTO>> getAll();

    ResponseEntity<PerformanceDTO> getById(Long id) throws NotFoundException;

    ResponseEntity<List<PerformanceDTO>> getAllContests();

    ResponseEntity<List<ContestPlaceDTO>> getContestDistribution();
    ResponseEntity<List<ContestPlaceDTO>> getDistributionByContestId(Long id);

    ResponseEntity<PerformanceDTO> savePerformance(PerformanceDTO performance) throws InvalidInputException;
    ResponseEntity<Void> deletePerformance(Long id);

    ResponseEntity<ContestPlaceDTO> saveContestDistribution(ContestPlaceDTO contestPlace) throws InvalidInputException;

    ResponseEntity<Void> deleteContestDistribution(IdKeyDTO key);

    ResponseEntity<Void> savePerformanceDistribution(PerformanceDistributionDTO distribution) throws
            InvalidInputException;

    ResponseEntity<List<PerformanceDistributionDTO>> getPerformanceDistribution();

    ResponseEntity<Void> deletePerformanceDistribution(PerformanceDistributionDTO distribution) throws
            InvalidInputException;

}
