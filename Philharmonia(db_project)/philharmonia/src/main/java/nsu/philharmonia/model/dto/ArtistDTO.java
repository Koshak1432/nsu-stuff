package nsu.philharmonia.model.dto;


import lombok.*;

@RequiredArgsConstructor
@Setter
@Getter
public class ArtistDTO {
    private Long id;
    private String name;
    private String surname;
}
