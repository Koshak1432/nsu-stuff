package nsu.fit.crackhashworker.listeners;

import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhashworker.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TaskListener {
    private final TaskService service;
    private final Logger logger = LoggerFactory.getLogger(TaskListener.class);

    public TaskListener(TaskService service) {
        this.service = service;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.task.name}"})
    public void createTask(CrackHashManagerRequest request) {
        logger.info("Got a message with id {} to crack {} hash",
                    request.getCrackHashManagerRequest().getRequestId(),
                    request.getCrackHashManagerRequest().getHash());
        service.crackHash(request);
        logger.info("Cracked");
    }
}
