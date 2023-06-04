package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.ContestPlaceDTO;
import nsu.philharmonia.model.dto.PerformanceDTO;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PerformanceService {
    ResponseEntity<List<PerformanceDTO>> getAll();

    ResponseEntity<PerformanceDTO> getById(Long id) throws NotFoundException;

    ResponseEntity<List<PerformanceDTO>> getAllContests();

    ResponseEntity<List<ContestPlaceDTO>> getDistribution();
    ResponseEntity<List<ContestPlaceDTO>> getDistributionByContestId(Long id);

    ResponseEntity<PerformanceDTO> savePerformance(PerformanceDTO performance) throws InvalidInputException;
    ResponseEntity<Void> deletePerformance(Long id);
}
