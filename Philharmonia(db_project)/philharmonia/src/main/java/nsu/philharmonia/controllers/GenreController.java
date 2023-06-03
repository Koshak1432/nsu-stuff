package nsu.philharmonia.controllers;


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
    public ResponseEntity<List<GenreDTO>> getByArtistId(@PathVariable("id") Long id) {
        return genreService.getByArtistId(id);
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAll() {
        return genreService.getAll();
    }

    //todo getByImpresarioId

}
