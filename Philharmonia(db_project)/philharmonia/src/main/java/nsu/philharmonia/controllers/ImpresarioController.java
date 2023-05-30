package nsu.philharmonia.controllers;


import jakarta.validation.constraints.Positive;
import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.ImpresarioDTO;
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

    @GetMapping("/artists/{id}")
    public ResponseEntity<List<ImpresarioDTO>> getByArtistId(@PathVariable("id") @Positive Long id) {
        return impresarioService.findByArtistId(id);
    }
}
