package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.ImpresarioDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ImpresarioService {
    ResponseEntity<List<ImpresarioDTO>> getAll();
}
