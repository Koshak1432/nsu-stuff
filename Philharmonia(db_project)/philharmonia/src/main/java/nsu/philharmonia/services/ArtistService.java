package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.entities.Artist;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ArtistService {
    ResponseEntity<List<ArtistDTO>> getAll();
    ResponseEntity<ArtistDTO> getArtistById(Long id) throws NotFoundException;
    ResponseEntity<ArtistDTO> addArtist(ArtistDTO artistDTO);
}
