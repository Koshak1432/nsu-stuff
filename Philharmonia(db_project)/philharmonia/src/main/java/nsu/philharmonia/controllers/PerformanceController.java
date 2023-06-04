package nsu.philharmonia.controllers;

import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.ContestPlaceDTO;
import nsu.philharmonia.model.dto.PerformanceDTO;
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

    @GetMapping("/contests/distribution")
    public ResponseEntity<List<ContestPlaceDTO>> getDistribution() {
        return performanceService.getDistribution();
    }

    @GetMapping("/contests/distribution/{id}")
    public ResponseEntity<List<ContestPlaceDTO>> getDistributionByContestId(@PathVariable("id") @Positive Long id) {
        return performanceService.getDistributionByContestId(id);
    }

    @GetMapping("/contests")
    public ResponseEntity<List<PerformanceDTO>> getAllContests() {
        return performanceService.getAllContests();
    }
}
