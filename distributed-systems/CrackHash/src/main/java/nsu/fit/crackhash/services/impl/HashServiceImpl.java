package nsu.fit.crackhash.services.impl;

import nsu.fit.crackhash.model.dto.CrackResponseDto;
import nsu.fit.crackhash.model.dto.HashDto;
import nsu.fit.crackhash.model.dto.StatusResponseDto;
import nsu.fit.crackhash.services.HashService;

import java.util.UUID;

public class HashServiceImpl implements HashService {
    @Override
    public CrackResponseDto crackHash(HashDto dto) {
        UUID uuid = UUID.randomUUID();
        // сохранить uuid в бд
        // отправить воркерам


        return new CrackResponseDto(uuid.toString());
    }

    @Override
    public StatusResponseDto getStatus(String requestId) {
        // поиск задачи в бд по requestId
        // return StatusResponseDto

        return null;
    }
}
