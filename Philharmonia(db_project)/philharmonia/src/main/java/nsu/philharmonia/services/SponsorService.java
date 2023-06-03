package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.SponsorDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SponsorService {

    ResponseEntity<List<SponsorDTO>> getAll();


}
