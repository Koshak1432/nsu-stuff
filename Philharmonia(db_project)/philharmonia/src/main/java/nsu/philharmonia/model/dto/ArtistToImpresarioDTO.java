package nsu.philharmonia.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class ArtistToImpresarioDTO {
    private ArtistDTO artist;
    private ImpresarioDTO impresario;
}
