package nsu.philharmonia.controllers;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.entities.Artist;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH)
@Validated
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/artists")
    public ResponseEntity<List<ArtistDTO>> getAllArtists() {
        return artistService.getAll();
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<ArtistDTO> getArtist(@PathVariable("id") @Positive(message = "Artist id must be positive") Long id) throws NotFoundException {
        return artistService.getArtistById(id);
    }
}