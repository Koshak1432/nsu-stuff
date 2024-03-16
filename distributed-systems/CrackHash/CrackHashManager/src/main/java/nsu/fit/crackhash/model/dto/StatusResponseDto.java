package nsu.fit.crackhash.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class StatusResponseDto implements Serializable {
    private String status;
    private List<String> data;
}
