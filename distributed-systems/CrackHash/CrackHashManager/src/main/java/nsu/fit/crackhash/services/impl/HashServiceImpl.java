package nsu.fit.crackhash.services.impl;

import nsu.fit.crackhash.config.Constants;
import nsu.fit.crackhash.model.CrackHashTask;
import nsu.fit.crackhash.model.WorkStatus;
import nsu.fit.crackhash.model.dto.*;
import nsu.fit.crackhash.model.entities.CrackTask;
import nsu.fit.crackhash.repositories.CrackTaskRepository;
import nsu.fit.crackhash.services.HashService;
import nsu.fit.crackhash.util.Util;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class HashServiceImpl implements HashService {
    private final int workerCount;
    private final String exchangeName;
    private final String taskRouting;
    private final AmqpTemplate rabbitTemplate;
    private final CrackTaskRepository crackTaskRepository;
    private final ConcurrentMap<String, CrackHashTask> tasks = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> schedulerTasks = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public HashServiceImpl(@Value("${worker.count}") int workerCount,
                           @Value("${rabbitmq.exchange.name}") String exchangeName,
                           @Value("${rabbitmq.routing.task.key}") String taskRouting,
                           @Qualifier("myRabbitTemplate") AmqpTemplate template,
                           CrackTaskRepository crackTaskRepository) {
        this.workerCount = workerCount;
        this.exchangeName = exchangeName;
        this.taskRouting = taskRouting;
        this.rabbitTemplate = template;
        this.crackTaskRepository = crackTaskRepository;
    }

    // todo what to do with concurrency and db data?
    @Override
    public void updateAnswers(CrackHashWorkerResponse response) {
        CrackHashWorkerResponse__1 actuallyResponse = response.getCrackHashWorkerResponse();
        CrackTask request = crackTaskRepository.findById(actuallyResponse.getRequestId()).orElseThrow(
                () -> new RuntimeException("Can't find request with id " + actuallyResponse.getRequestId()));
        request.getWords().addAll(actuallyResponse.getAnswers().getWords());
        // todo как-то надо понимать, когда будет реди
        crackTaskRepository.save(request);
    }

    @Override
    public CrackResponseDto crackHash(HashDto dto) {
        String uuid = UUID.randomUUID().toString();
        CrackTask task = new CrackTask(uuid, dto.getHash(), dto.getMaxLength(), new ArrayList<>(),
                                       WorkStatus.IN_PROGRESS);
        crackTaskRepository.save(task);
        // need to replicate task

        for (int workerNum = 0; workerNum < workerCount; ++workerNum) {
            CrackHashManagerRequest crackHashManagerRequest = formRequestToWorker(dto, uuid, workerNum);
            sendTaskToQueue(crackHashManagerRequest);
        }
        schedulerTasks.put(uuid, scheduler.scheduleAtFixedRate(() -> checkTimeout(uuid), Constants.CHECK_PERIOD_MILLIS,
                                                               Constants.CHECK_PERIOD_MILLIS, TimeUnit.MILLISECONDS));
        return new CrackResponseDto(uuid);
    }

    private void sendTaskToQueue(CrackHashManagerRequest crackHashManagerRequest) {
        rabbitTemplate.convertAndSend(exchangeName, taskRouting, crackHashManagerRequest, message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }

    // todo если пришли все ответы, то тоже отменить проверку надо по-хорошему?
    private void checkTimeout(String requestId) {
        long currentTime = System.currentTimeMillis();
        CrackHashTask task = tasks.get(requestId);
        if (currentTime - task.getTaskStartTime() > Constants.TASK_TIMEOUT_MILLIS) {
            task.setTimeoutExpired();
            schedulerTasks.get(requestId).cancel(true);
        }
    }

    private CrackHashManagerRequest formRequestToWorker(HashDto dto, String requestId, int workerNum) {
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
        CrackTask task = crackTaskRepository.findById(requestId).orElseThrow(
                () -> new RuntimeException("Couldn't find task with id " + requestId));
        return new StatusResponseDto(task.getStatus().name(), task.getWords().isEmpty() ? null : task.getWords());
    }
}
