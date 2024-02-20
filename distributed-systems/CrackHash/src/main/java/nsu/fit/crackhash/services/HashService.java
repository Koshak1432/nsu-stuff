package nsu.fit.crackhash.services;

import nsu.fit.crackhash.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhash.model.dto.CrackResponseDto;
import nsu.fit.crackhash.model.dto.HashDto;
import nsu.fit.crackhash.model.dto.StatusResponseDto;
import org.springframework.stereotype.Service;

public interface HashService {
    CrackResponseDto crackHash(HashDto dto);
    StatusResponseDto getStatus(String requestId);
}
