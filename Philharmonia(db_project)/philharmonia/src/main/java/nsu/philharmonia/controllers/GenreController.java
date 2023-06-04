package nsu.philharmonia.controllers;


import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.GenreDTO;
import nsu.philharmonia.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH + "/genres")
@Validated
public class GenreController {
    private final GenreService genreService;


    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<List<GenreDTO>> getByArtistId(@PathVariable("id") @Positive Long id) {
        return genreService.getByArtistId(id);
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAll() {
        return genreService.getAll();
    }

    @PostMapping
    public ResponseEntity<GenreDTO> addGenre(@RequestBody GenreDTO genre) {
        return genreService.saveGenre(genre);
    }

    @PutMapping
    public ResponseEntity<GenreDTO> updateGenre(@RequestBody GenreDTO genre) {
        return genreService.saveGenre(genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable("id") @Positive Long id) {
        return genreService.deleteGenre(id);
    }

    //todo getByImpresarioId

}
