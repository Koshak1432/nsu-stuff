package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.dto.ArtistToGenreDTO;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ArtistService {
    ResponseEntity<List<ArtistDTO>> getAll();
    ResponseEntity<ArtistDTO> getArtistById(Long id) throws NotFoundException;
    ResponseEntity<ArtistDTO> addArtist(ArtistDTO artistDTO);

    ResponseEntity<Void> updateArtist(ArtistDTO artistDTO) throws NotFoundException;

    ResponseEntity<Void> deleteArtist(Long id);

    ResponseEntity<Void> saveArtistToGenre(ArtistToGenreDTO dto) throws InvalidInputException;

    ResponseEntity<List<ArtistToGenreDTO>> getArtistToGenres();
}
