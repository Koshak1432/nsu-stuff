package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.SponsorDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SponsorService {

    ResponseEntity<List<SponsorDTO>> getAll();

    ResponseEntity<SponsorDTO> saveSponsor(SponsorDTO sponsor);
    ResponseEntity<Void> deleteSponsor(Long id);

}
