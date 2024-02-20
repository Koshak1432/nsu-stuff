package nsu.fit.crackhash.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class HashDto implements Serializable {
    private String hash;
    private int maxLength;
}
