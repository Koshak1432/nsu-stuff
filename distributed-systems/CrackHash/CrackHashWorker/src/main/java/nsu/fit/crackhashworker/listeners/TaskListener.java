package nsu.fit.crackhashworker.listeners;

import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhashworker.services.TaskService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@EnableRabbit
@Component
public class TaskListener {
    private final TaskService service;

    public TaskListener(TaskService service) {
        this.service = service;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.task.name}"})
    public void createTask(CrackHashManagerRequest request) {
        service.crackHash(request);
    }
}
