package nsu.fit.crackhash.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CrackResponseDto implements Serializable {
    private String requestId;
}
