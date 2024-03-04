package nsu.fit.crackhash.services.impl;

import nsu.fit.crackhash.config.Constants;
import nsu.fit.crackhash.model.CrackHashTask;
import nsu.fit.crackhash.model.WorkStatus;
import nsu.fit.crackhash.model.dto.*;
import nsu.fit.crackhash.services.HashService;
import nsu.fit.crackhash.util.Util;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class HashServiceImpl implements HashService {
    private final int workerCount;
    private final String exchangeName;
    private final String taskRouting;
    private final AmqpTemplate rabbitTemplate;
    private final ConcurrentMap<String, CrackHashTask> tasks = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> schedulerTasks = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public HashServiceImpl(@Value("${worker.count}") int workerCount,
                           @Value("${rabbitmq.exchange.name}") String exchangeName,
                           @Value("${rabbitmq.routing.task.key}") String taskRouting,
                           @Qualifier("myRabbitTemplate") AmqpTemplate template) {
        this.workerCount = workerCount;
        this.exchangeName = exchangeName;
        this.taskRouting = taskRouting;
        this.rabbitTemplate = template;
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
            CrackHashManagerRequest crackHashManagerRequest = formRequest(dto, uuid, workerNum);
            rabbitTemplate.convertAndSend(exchangeName, taskRouting, crackHashManagerRequest);
        }
        schedulerTasks.put(uuid, scheduler.scheduleAtFixedRate(() -> checkTimeout(uuid), Constants.CHECK_PERIOD_MILLIS,
                                                               Constants.CHECK_PERIOD_MILLIS, TimeUnit.MILLISECONDS));
        return new CrackResponseDto(uuid);
    }

    private void checkTimeout(String requestId) {
        long currentTime = System.currentTimeMillis();
        CrackHashTask task = tasks.get(requestId);
        if (currentTime - task.getTaskStartTime() > Constants.TASK_TIMEOUT_MILLIS) {
            task.setTimeoutExpired();
            schedulerTasks.get(requestId).cancel(true);
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
