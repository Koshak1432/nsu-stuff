package nsu.fit.crackhash.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nsu.fit.crackhash.model.WorkStatus;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class StatusResponseDto implements Serializable {
    private String status;
    private String[] data;
}
