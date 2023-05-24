package nsu.philharmonia.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class SponsorDTO {
    private Long id;
    private String name;
    private String surname;
}
