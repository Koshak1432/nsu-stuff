package nsu.philharmonia.controllers;

import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.dto.ArtistToGenreDTO;
import nsu.philharmonia.model.exceptions.InvalidInputException;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH + "/artists")
@Validated
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping()
    public ResponseEntity<List<ArtistDTO>> getAllArtists() {
        return artistService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtist(@PathVariable("id") @Positive(message = "Artist id must be positive") Long id) throws NotFoundException {
        return artistService.getArtistById(id);
    }

    @PostMapping
    public ResponseEntity<ArtistDTO> postArtist(@RequestBody ArtistDTO artistDTO) {
        return artistService.addArtist(artistDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateArtist(@RequestBody ArtistDTO artistDTO) throws NotFoundException {
        return artistService.updateArtist(artistDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable("id") @Positive Long id) {
        return artistService.deleteArtist(id);
    }

    @GetMapping("/distribution")
    public ResponseEntity<List<ArtistToGenreDTO>> getArtistToGenres() {
        return artistService.getArtistToGenres();
    }

    @PostMapping("/distribution")
    public ResponseEntity<Void> addArtistToGenres(@RequestBody ArtistToGenreDTO dto) throws InvalidInputException {
        return artistService.saveArtistToGenre(dto);
    }
}
