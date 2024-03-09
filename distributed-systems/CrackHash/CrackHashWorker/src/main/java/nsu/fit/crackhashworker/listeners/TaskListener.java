package nsu.fit.crackhashworker.listeners;

import com.rabbitmq.client.Channel;
import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhashworker.services.TaskService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@EnableRabbit
@Component
public class TaskListener {
    private final TaskService service;

    public TaskListener(TaskService service) {
        this.service = service;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.task.name}"})
    public void createTask(CrackHashManagerRequest request, Channel channel,
                           @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        System.out.println("Got message from queue");
        service.crackHash(request);
        System.out.println("Going to ack the message");
        channel.basicAck(tag, false);
    }
}
