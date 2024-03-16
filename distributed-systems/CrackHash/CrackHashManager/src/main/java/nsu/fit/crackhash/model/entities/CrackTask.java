package nsu.fit.crackhash.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.fit.crackhash.model.WorkStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CrackTask {
    @Id
    private String requestId;

    private String hash;

    private int maxLength;

    private List<String> words;

    private WorkStatus status;

    private int partsRemaining;

    private boolean isSentToQueue = false;

    private long taskCreated;
}
