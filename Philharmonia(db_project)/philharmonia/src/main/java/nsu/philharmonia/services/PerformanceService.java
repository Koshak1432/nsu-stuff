package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.PerformanceDTO;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PerformanceService {
    ResponseEntity<List<PerformanceDTO>> getAll();

    ResponseEntity<PerformanceDTO> getById(Long id) throws NotFoundException;
}