package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.GenreDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GenreService {

    ResponseEntity<List<GenreDTO>> getByArtistId(Long artistId);
}
