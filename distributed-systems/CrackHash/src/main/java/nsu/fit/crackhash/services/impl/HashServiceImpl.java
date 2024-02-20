package nsu.fit.crackhash.services.impl;

import nsu.fit.crackhash.config.Constants;
import nsu.fit.crackhash.model.dto.*;
import nsu.fit.crackhash.services.HashService;
import nsu.fit.crackhash.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class HashServiceImpl implements HashService {
    private final RestTemplate restTemplate;
    private final ConcurrentMap<UUID, Integer> hashMap = new ConcurrentHashMap<>();
    private final int workerCount;

    public HashServiceImpl(RestTemplate restTemplate, @Value("${WORKER_COUNT}") int workerCount) {
        this.restTemplate = restTemplate;
        this.workerCount = workerCount;
    }

    @Override
    public CrackResponseDto crackHash(HashDto dto) {
        UUID uuid = UUID.randomUUID();
        for (int i = 0; i < workerCount; ++i) {
            CrackHashManagerRequest__1 request = fillRequest(dto.getHash(), dto.getMaxLength(), uuid, i);
            CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
            crackHashManagerRequest.setCrackHashManagerRequest(request);
            ResponseEntity<Void> response = restTemplate.postForEntity(Constants.WORKER_URL, crackHashManagerRequest, Void.class);

        }

        // сохранить uuid в бд
        // отправить воркерам


        return new CrackResponseDto(uuid.toString());
    }



    private CrackHashManagerRequest__1 fillRequest(String hash, int maxLen, UUID uuid, int workerId) {
        CrackHashManagerRequest__1 request = new CrackHashManagerRequest__1();
        request.setHash(hash);
        request.setAlphabet(Util.getAlphabetFromString(Constants.ALPHABET));
        request.setRequestId(uuid.toString());
        request.setMaxLength(maxLen);
        request.setPartCount(workerCount);
        request.setPartNumber(Util.countAllCombinations(Constants.ALPHABET, maxLen) / workerCount * workerId);
        return request;
    }

    @Override
    public StatusResponseDto getStatus(String requestId) {
        // поиск задачи в бд по requestId
        // return StatusResponseDto

        return null;
    }
}
