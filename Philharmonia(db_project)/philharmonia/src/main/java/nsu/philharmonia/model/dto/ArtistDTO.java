package nsu.philharmonia.model.dto;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import java.util.Set;

@RequiredArgsConstructor
@Setter
@Getter
public class ArtistDTO {
    private Long id;
    private String name;
    private String surname;
    private Set<GenreDTO> genres;
}
