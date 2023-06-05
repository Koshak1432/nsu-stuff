package nsu.philharmonia.controllers;


import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.dto.ArtistToImpresarioDTO;
import nsu.philharmonia.model.dto.ImpresarioDTO;
import nsu.philharmonia.model.exceptions.NotFoundException;
import nsu.philharmonia.services.ImpresarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH + "/impresarios")
@Validated
public class ImpresarioController {
    private final ImpresarioService impresarioService;

    @Autowired
    public ImpresarioController(ImpresarioService impresarioService) {
        this.impresarioService = impresarioService;
    }


    @GetMapping
    public ResponseEntity<List<ImpresarioDTO>> getAll() {
        return impresarioService.getAll();
    }

    @GetMapping("/by-artist/{id}")
    public ResponseEntity<List<ImpresarioDTO>> getByArtistId(@PathVariable("id") @Positive Long id) {
        return impresarioService.getByArtistId(id);
    }

    @GetMapping("/{id}/artists")
    public ResponseEntity<List<ArtistDTO>> getArtistsByImpresarioId(@PathVariable("id") @Positive Long id) throws
            NotFoundException {
        return impresarioService.getArtistsByImpresarioId(id);
    }

    @PostMapping
    public ResponseEntity<ImpresarioDTO> addImpresario(@RequestBody ImpresarioDTO impresario) {
        return impresarioService.addImpresario(impresario);
    }

    @PutMapping
    public ResponseEntity<ImpresarioDTO> updateImpresario(@RequestBody ImpresarioDTO impresario) {
        return impresarioService.addImpresario(impresario);
        //todo maybe update
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImpresario(@PathVariable("id") @Positive Long id) {
        return impresarioService.deleteImpresario(id);
    }

    @GetMapping("/distribution")
    public ResponseEntity<List<ArtistToImpresarioDTO>> getArtistToImpresario() {
        return impresarioService.getArtistToImpresario();
    }
}
