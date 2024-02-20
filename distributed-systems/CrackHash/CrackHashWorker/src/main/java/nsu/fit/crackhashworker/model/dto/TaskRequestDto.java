package nsu.fit.crackhashworker.model.dto;

import java.io.Serializable;

public class TaskRequestDto implements Serializable {
    private String alphabet;
    private String hash;
    private int maxLength;
    private int partCount; // Общее количество частей
    private int partNumber; // Номер части запроса
}
