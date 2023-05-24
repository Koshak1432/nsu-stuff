package nsu.philharmonia.controllers;

import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
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
@RequestMapping(Constants.BASE_API_PATH)
@Validated
public class PerformanceController {

    private final PerformanceService performanceService;

    @Autowired
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/performances")
    public ResponseEntity<List<PerformanceDTO>> getAllPerformances() {
        return performanceService.getAll();
    }

    @GetMapping("/performance/{id}")
    public ResponseEntity<PerformanceDTO> getAllPerformances(@PathVariable("id") @Positive Long id) throws
            NotFoundException {
        return performanceService.getById(id);
    }
}
