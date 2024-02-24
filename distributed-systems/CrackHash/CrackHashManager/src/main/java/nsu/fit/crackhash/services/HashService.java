package nsu.fit.crackhash.services;

import nsu.fit.crackhash.model.dto.*;

public interface HashService {
    CrackResponseDto crackHash(HashDto dto);
    StatusResponseDto getStatus(String requestId);
    void updateAnswers(CrackHashWorkerResponse response);
}
