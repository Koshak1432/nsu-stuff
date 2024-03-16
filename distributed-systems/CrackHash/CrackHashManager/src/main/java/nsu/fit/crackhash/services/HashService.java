package nsu.fit.crackhash.services;

import nsu.fit.crackhash.model.dto.CrackHashWorkerResponse;
import nsu.fit.crackhash.model.dto.CrackResponseDto;
import nsu.fit.crackhash.model.dto.HashDto;
import nsu.fit.crackhash.model.dto.StatusResponseDto;

public interface HashService {
    CrackResponseDto crackHash(HashDto dto);

    StatusResponseDto getStatus(String requestId);

    void updateAnswers(CrackHashWorkerResponse response);
}
