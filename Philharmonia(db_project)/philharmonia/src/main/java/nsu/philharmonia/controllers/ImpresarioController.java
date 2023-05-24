package nsu.philharmonia.controllers;


import nsu.philharmonia.config.Constants;
import nsu.philharmonia.model.dto.ImpresarioDTO;
import nsu.philharmonia.services.ImpresarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(Constants.BASE_API_PATH)
@Validated
public class ImpresarioController {
    private final ImpresarioService impresarioService;

    @Autowired
    public ImpresarioController(ImpresarioService impresarioService) {
        this.impresarioService = impresarioService;
    }


    @GetMapping("/impresarios")
    public ResponseEntity<List<ImpresarioDTO>> getAll() {
        return impresarioService.getAll();
    }
}
