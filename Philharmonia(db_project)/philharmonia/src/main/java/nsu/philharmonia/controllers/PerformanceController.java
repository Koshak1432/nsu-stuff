package nsu.philharmonia.controllers;

import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.*;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.services.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH + "/performances")
@Validated
public class PerformanceController {

    private final PerformanceService performanceService;

    @Autowired
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping
    public ResponseEntity<List<PerformanceDTO>> getAllPerformances() {
        return performanceService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDTO> getById(@PathVariable("id") @Positive Long id) throws
            NotFoundException {
        return performanceService.getById(id);
    }

    @PostMapping
    public ResponseEntity<PerformanceDTO> addPerformance(@RequestBody PerformanceDTO performance) throws
            InvalidInputException {
        return performanceService.savePerformance(performance);
    }

    @PutMapping
    public ResponseEntity<PerformanceDTO> updatePerformance(@RequestBody PerformanceDTO performance) throws
            InvalidInputException {
        return performanceService.savePerformance(performance);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable("id") @Positive Long id) {
        return performanceService.deletePerformance(id);
    }


    // contests
    @GetMapping("/contests")
    public ResponseEntity<List<PerformanceDTO>> getAllContests() {
        return performanceService.getAllContests();
    }


    @GetMapping("/contests/distribution")
    public ResponseEntity<List<ContestPlaceDTO>> getContestsDistribution() {
        return performanceService.getContestDistribution();
    }

    @PostMapping("/contests/distribution")
    public ResponseEntity<ContestPlaceDTO> addContestDistribution(@RequestBody ContestPlaceDTO contestPlace) throws
            InvalidInputException {
        return performanceService.saveContestDistribution(contestPlace);
    }

    @PutMapping("/contests/distribution")
    public ResponseEntity<ContestPlaceDTO> updateContestDistribution(@RequestBody ContestPlaceDTO contestPlace) throws
            InvalidInputException {
        return performanceService.saveContestDistribution(contestPlace);
    }

    @DeleteMapping("/contests/distribution")
    public ResponseEntity<Void> deleteContestDistribution(@RequestBody IdKeyDTO key) {
        return performanceService.deleteContestDistribution(key);
    }

    @GetMapping("/contests/distribution/{id}")
    public ResponseEntity<List<ContestPlaceDTO>> getDistributionByContestId(@PathVariable("id") @Positive Long id) {
        return performanceService.getDistributionByContestId(id);
    }


    // performance disctr
    @GetMapping("/distribution")
    public ResponseEntity<List<PerformanceDistributionDTO>> getPerformancesDistribution() {
        return performanceService.getPerformanceDistribution();
    }

    @PostMapping("/distribution")
    public ResponseEntity<Void> addPerformanceDistribution(@RequestBody PerformanceDistributionDTO distribution) throws
            InvalidInputException {
        return performanceService.savePerformanceDistribution(distribution);
    }

    @DeleteMapping("/distribution")
    public ResponseEntity<Void> deletePerformanceDistribution(@RequestBody PerformanceDistributionDTO distribution) throws
            InvalidInputException {
        return performanceService.deletePerformanceDistribution(distribution);
    }



}
