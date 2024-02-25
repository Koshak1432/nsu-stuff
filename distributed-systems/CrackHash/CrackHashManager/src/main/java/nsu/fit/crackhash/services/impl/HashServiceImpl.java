package nsu.fit.crackhash.services.impl;

import nsu.fit.crackhash.config.Constants;
import nsu.fit.crackhash.model.CrackHashTask;
import nsu.fit.crackhash.model.WorkStatus;
import nsu.fit.crackhash.model.dto.*;
import nsu.fit.crackhash.services.HashService;
import nsu.fit.crackhash.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class HashServiceImpl implements HashService {
    private final RestTemplate restTemplate;
    private final int workerCount;
    private final ConcurrentMap<String, CrackHashTask> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public HashServiceImpl(RestTemplate restTemplate, @Value("${worker_count}") int workerCount) {
        this.restTemplate = restTemplate;
        this.workerCount = workerCount;
    }

    @Override
    public void updateAnswers(CrackHashWorkerResponse response) {
        CrackHashWorkerResponse__1 actuallyResponse = response.getCrackHashWorkerResponse();
        CrackHashTask task = tasks.get(actuallyResponse.getRequestId());
        task.addWords(actuallyResponse.getAnswers().getWords());
    }

    @Override
    public CrackResponseDto crackHash(HashDto dto) {
        String uuid = UUID.randomUUID().toString();
        tasks.put(uuid, new CrackHashTask(workerCount));
        for (int workerNum = 0; workerNum < workerCount; ++workerNum) {
            String workerUrl = MessageFormat.format("http://crackhash-worker-{0}:808{0}" + Constants.WORKER_TASK_URI,
                                                    workerNum + 1);
            CrackHashManagerRequest crackHashManagerRequest = formRequest(dto, uuid, workerNum);
            ResponseEntity<Void> response = restTemplate.postForEntity(URI.create(workerUrl), crackHashManagerRequest,
                                                                       Void.class);
        }
        scheduler.scheduleAtFixedRate(() -> checkTimeout(uuid), Constants.CHECK_PERIOD_MILLIS,
                                      Constants.CHECK_PERIOD_MILLIS, TimeUnit.MILLISECONDS);

        return new CrackResponseDto(uuid);
    }

    private void checkTimeout(String requestId) {
        long currentTime = System.currentTimeMillis();
        CrackHashTask task = tasks.get(requestId);
        if (currentTime - task.getTaskStartTime() > Constants.TASK_TIMEOUT_MILLIS) {
            task.setTimeoutExpired();
        }
    }

    private CrackHashManagerRequest formRequest(HashDto dto, String requestId, int workerNum) {
        CrackHashManagerRequest__1 request = new CrackHashManagerRequest__1();
        request.setHash(dto.getHash());
        request.setAlphabet(Util.getAlphabetFromString(Constants.ALPHABET));
        request.setRequestId(requestId);
        request.setMaxLength(dto.getMaxLength());
        request.setPartNumber(workerNum);
        request.setPartCount(workerCount);

        CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
        crackHashManagerRequest.setCrackHashManagerRequest(request);
        return crackHashManagerRequest;
    }

    @Override
    public StatusResponseDto getStatus(String requestId) {
        List<String> data = null;
        CrackHashTask task = tasks.get(requestId);
        WorkStatus status = task.getStatus();
        if (status == WorkStatus.READY) {
            data = task.getWords();
        }
        return new StatusResponseDto(status.name(), data);
    }
}
