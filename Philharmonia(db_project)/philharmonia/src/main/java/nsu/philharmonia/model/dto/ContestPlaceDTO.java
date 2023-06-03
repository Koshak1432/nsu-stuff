package nsu.philharmonia.model.dto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

@RequiredArgsConstructor
@Setter
@Getter
public class ContestPlaceDTO {
    private IdKeyDTO id;
    private Integer place;
}
