package nsu.philharmonia.services;

import nsu.philharmonia.model.dto.ArtistDTO;
import nsu.philharmonia.model.dto.ImpresarioDTO;
import nsu.philharmonia.model.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ImpresarioService {
    ResponseEntity<List<ImpresarioDTO>> getAll();

    ResponseEntity<List<ImpresarioDTO>> getByArtistId(Long id);

    ResponseEntity<List<ArtistDTO>> getArtistsByImpresarioId(Long id) throws NotFoundException;

    ResponseEntity<ImpresarioDTO> addImpresario(ImpresarioDTO impresario);
    ResponseEntity<Void> deleteImpresario(Long id);
}
