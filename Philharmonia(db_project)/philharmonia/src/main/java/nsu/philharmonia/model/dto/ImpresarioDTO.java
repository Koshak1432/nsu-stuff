package nsu.philharmonia.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@RequiredArgsConstructor
@Setter
@Getter
public class ImpresarioDTO {
    private Long id;
    private String name;
    private String surname;
//    private Set<ArtistDTO> artists;
}
