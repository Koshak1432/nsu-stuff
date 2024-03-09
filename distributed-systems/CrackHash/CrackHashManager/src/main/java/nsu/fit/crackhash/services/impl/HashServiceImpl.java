package nsu.fit.crackhash.services.impl;

import nsu.fit.crackhash.config.Constants;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class HashServiceImpl implements HashService {
    private final int workerCount;
    private final String exchangeName;
    private final String taskRouting;
    private final AmqpTemplate rabbitTemplate;
    private final CrackTaskRepository crackTaskRepository;
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

    @Override
    public void updateAnswers(CrackHashWorkerResponse response) {
        CrackHashWorkerResponse__1 actuallyResponse = response.getCrackHashWorkerResponse();
        CrackTask task = crackTaskRepository.findById(actuallyResponse.getRequestId()).orElseThrow(
                () -> new RuntimeException("Can't find task with id " + actuallyResponse.getRequestId()));
        System.out.println("UPDATING ANSWERS: " + actuallyResponse.getRequestId() + " to " + actuallyResponse.getAnswers().getWords());
        task.getWords().addAll(actuallyResponse.getAnswers().getWords());
        task.setPartsRemaining(task.getPartsRemaining() - 1);
        if (task.getPartsRemaining() == 0) {
            task.setStatus(WorkStatus.READY);
            schedulerTasks.get(task.getRequestId()).cancel(true);
        }
        crackTaskRepository.save(task);
    }

    @Override
    public CrackResponseDto crackHash(HashDto dto) {
        String requestId = UUID.randomUUID().toString();
        CrackTask task = new CrackTask(requestId, dto.getHash(), dto.getMaxLength(), new ArrayList<>(),
                                       WorkStatus.IN_PROGRESS, workerCount, false, System.currentTimeMillis());
        crackTaskRepository.save(task);
        schedulerTasks.put(requestId,
                           scheduler.scheduleAtFixedRate(() -> checkTimeout(requestId), Constants.CHECK_PERIOD_MILLIS,
                                                         Constants.CHECK_PERIOD_MILLIS, TimeUnit.MILLISECONDS));
        return new CrackResponseDto(requestId);
    }

    private void sendTaskToQueue(CrackHashManagerRequest crackHashManagerRequest) {
        rabbitTemplate.convertAndSend(exchangeName, taskRouting, crackHashManagerRequest, message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }

    private void checkTimeout(String requestId) {
        long currentTime = System.currentTimeMillis();
        CrackTask task = crackTaskRepository.findById(requestId).orElseThrow(
                () -> new RuntimeException("Couldn't find task with id : " + requestId));
        if (currentTime - task.getTaskCreated() > Constants.TASK_TIMEOUT_MILLIS) {
            task.setStatus(WorkStatus.ERROR);
            schedulerTasks.get(requestId).cancel(true);
        }
    }

    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    private void sendTasksToWorkers() {
        List<CrackTask> tasks = crackTaskRepository.findCrackTasksByIsSentToQueue(false);
        System.out.println("SendingToWorkers moment, worker count: " + workerCount);
        for (CrackTask task : tasks) {
            System.out.println(
                    "hash: " + task.getHash() + ", id: " + task.getRequestId() + ", status: " + task.getStatus().name());
            for (int workerNum = 0; workerNum < workerCount; ++workerNum) {
                CrackHashManagerRequest crackHashManagerRequest = formRequestToWorker(task, workerNum);
                sendTaskToQueue(crackHashManagerRequest);
            }
            task.setSentToQueue(true);
            crackTaskRepository.save(task);
        }
    }

    private CrackHashManagerRequest formRequestToWorker(CrackTask task, int workerNum) {
        CrackHashManagerRequest__1 request = new CrackHashManagerRequest__1();
        request.setHash(task.getHash());
        request.setAlphabet(Util.getAlphabetFromString(Constants.ALPHABET));
        request.setRequestId(task.getRequestId());
        request.setMaxLength(task.getMaxLength());
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
